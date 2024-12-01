package com.fr3ts0n.ecu.gui.androbd

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.fr3ts0n.ecu.gui.androbd.CommService.STATE
import com.fr3ts0n.ecu.prot.obd.ElmProt
import com.fr3ts0n.ecu.prot.obd.ObdProt
import com.fr3ts0n.pvs.PvChangeEvent
import com.fr3ts0n.pvs.PvChangeListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class WorkerService : Service() {
    private val binder = WorkerServiceBinder()
    private val receiver = Receiver()
    private lateinit var mCommService: CommService
    private var mMainActivityHandler: Handler? = null
    private var mIsNotificationVisible = false
    private var mLastBluetoothDeviceAddress = ""
    private var mIsCommServiceConnected = false

    private lateinit var mHandler: Handler
    private val mPvListListeners: ArrayList<PvListChangeListener> = ArrayList()
    private val mPropListener = PropChangeListener()
    private var mIsPropListenerSubscribed = false

    private val mCoroutineScope = CoroutineScope(Dispatchers.Main)
    private val mSavedMessages = LastMessages()

    inner class WorkerServiceBinder : Binder() {
        fun setHandler(handler: Handler) = setMainActivityHandler(handler)
        fun removeHandler() = removeMainActivityHandler()
        fun connectToBluetooth(device: BluetoothDevice, isSecure: Boolean) =
            connectBT(device, isSecure)

        fun sendSavedDataToHandler() = requestEventsToActivityHandler()
        fun connectToUsb() = connectDeviceUsb()
        fun connectToNetwork(address: String?, port: Int) = connectNetworkDevice(address, port)
        fun connectToFile() = connectFile()
    }

    inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(ACTION_STOP_SERVICE)) {
                if (mIsCommServiceConnected) stopCommService()
                stop()
            }
        }
    }

    private inner class PvListChangeListener(val mAdapter: ObdItemAdapter) : PvChangeListener {
        init {
            mAdapter.pvs.addPvChangeListener(this)
        }

        override fun pvChanged(event: PvChangeEvent) {
            mCoroutineScope.launch {
                if (event.isChildEvent) return@launch

                when (event.type) {
                    PvChangeEvent.PV_ADDED -> mAdapter.setPvList(mAdapter.pvs)
                    PvChangeEvent.PV_CLEARED -> mAdapter.clear()
                }

                mMainActivityHandler?.obtainMessage(MainActivity.MESSAGE_DATA_ITEMS_CHANGED)
                    ?.apply {
                        obj = event

                        mSavedMessages.addMessage(this)

                        sendToTarget()
                    }
            }
        }

        fun unsubscribeFromPvList() {
            mAdapter.pvs.removePvChangeListener(this)
        }
    }

    private inner class PropChangeListener() : PropertyChangeListener {
        private fun sendToMainActivityHandler(eventType: Int, evt: PropertyChangeEvent) {
            mMainActivityHandler?.let { mainActivityHandler ->
                mainActivityHandler.obtainMessage(eventType).apply {
                    obj = evt
                    sendToTarget()
                }
            }
        }

        override fun propertyChange(evt: PropertyChangeEvent?) {
            mCoroutineScope.launch {
                val propertyEvent = evt ?: return@launch

                when (propertyEvent.propertyName) {
                    ElmProt.PROP_STATUS -> sendToMainActivityHandler(
                        MainActivity.MESSAGE_OBD_STATE_CHANGED,
                        propertyEvent
                    )

                    ElmProt.PROP_NUM_CODES -> sendToMainActivityHandler(
                        MainActivity.MESSAGE_OBD_NUMCODES,
                        propertyEvent
                    )

                    ElmProt.PROP_ECU_ADDRESS -> sendToMainActivityHandler(
                        MainActivity.MESSAGE_OBD_ECUS,
                        propertyEvent
                    )

                    ObdProt.PROP_NRC -> sendToMainActivityHandler(
                        MainActivity.MESSAGE_OBD_NRC,
                        propertyEvent
                    )
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        onRebind(intent)

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean = true

    override fun onRebind(intent: Intent?) {
        resubscribeToPvListChanges()
    }

    override fun onCreate() {
        isRunning = true

        createNotificationChannel()

        val intentFilter = IntentFilter(ACTION_STOP_SERVICE)
        val receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED

        ContextCompat.registerReceiver(this, receiver, intentFilter, receiverFlags)
    }

    override fun onDestroy() {
        try {
            CommService.elm.goToSleep();
            Thread.sleep(100, 0);
        } catch (e: InterruptedException) {
            Log.v(TAG, e.localizedMessage ?: "")
        }

        ObdProt.PidPvs.clear()
        ObdProt.VidPvs.clear()
        ObdProt.tCodes.clear()

        unsubscribeFromPvListChanges()

        stopCommService()

        unregisterReceiver(receiver)

        isRunning = false

        if (mIsNotificationVisible) {
            stopForeground()
        }

        mCoroutineScope.cancel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mHandler = object : Handler(mainLooper) {
            override fun handleMessage(msg: Message) {

                when (msg.what) {
                    MainActivity.MESSAGE_STATE_CHANGE -> {
                        when (val msgState = msg.obj as STATE) {
                            STATE.CONNECTING, STATE.CONNECTED -> {
                                startForeground()
                                mIsNotificationVisible = true

                                if (msgState == STATE.CONNECTED) mIsCommServiceConnected = true
                            }

                            else -> {
                                if (mIsNotificationVisible) {
                                    stopForeground()
                                    mIsNotificationVisible = false
                                }

                                mIsCommServiceConnected = false
                                mLastBluetoothDeviceAddress = ""
                                mSavedMessages.clear()
                            }
                        }
                    }
                }

                mSavedMessages.addMessage(msg)

                mMainActivityHandler?.obtainMessage(msg.what, msg.obj)?.apply {
                    data = msg.data
                    sendToTarget()
                }
            }
        }

        startForeground()

        return START_NOT_STICKY
    }

    fun stop() {
        stopCommService()

        if (mIsNotificationVisible) {
            stopForeground()
        }

        stopSelf()
    }

    private fun requestEventsToActivityHandler() {
        mMainActivityHandler?.let { handler ->
            mSavedMessages.sendAllFromHandler(handler)
        }
    }

    fun connectBT(device: BluetoothDevice, isSecure: Boolean) {
        if (mIsCommServiceConnected && mLastBluetoothDeviceAddress == device.address && mMainActivityHandler != null) {
            requestEventsToActivityHandler()
            return
        }

        mCommService = BtCommService(this, mHandler).also { btService ->
            mLastBluetoothDeviceAddress = device.address
            btService.connect(device, isSecure)
        }
    }


    fun connectDeviceUsb() {
        mCommService = UsbCommService(this, mHandler).also { usbService ->
            usbService.connect(UsbDeviceListActivity.selectedPort, true)
        }
    }

    fun connectNetworkDevice(address: String?, port: Int) {
        mCommService = NetworkCommService(this, mHandler).also { networkService ->
            stopCommService()
            networkService.connect(address, port)
        }
    }

    fun connectFile() {
        resubscribeToPvListChanges()
        if (mIsCommServiceConnected) stopCommService()
    }

    private fun stopCommService() {
        if (mIsCommServiceConnected) {
            mCommService.stop()
            mIsCommServiceConnected = false
        }
    }

    private fun subscribeToPvListChanges() {
        mPvListListeners.apply {
            add(PvListChangeListener(MainActivity.mPidAdapter))
            add(PvListChangeListener(MainActivity.mVidAdapter))
            add(PvListChangeListener(MainActivity.mTidAdapter))
            add(PvListChangeListener(MainActivity.mDfcAdapter))
            add(PvListChangeListener(MainActivity.mPluginDataAdapter))
        }

        CommService.elm.addPropertyChangeListener(mPropListener)
        mIsPropListenerSubscribed = true;
    }

    private fun resubscribeToPvListChanges() {
        unsubscribeFromPvListChanges()
        subscribeToPvListChanges()
    }

    private fun unsubscribeFromPvListChanges() {
        mPvListListeners.apply {
            forEach { it.unsubscribeFromPvList() }
            clear()
        }

        if (mIsPropListenerSubscribed) {
            CommService.elm.removePropertyChangeListener(mPropListener)
            mIsPropListenerSubscribed = false
        }
    }

    fun setMainActivityHandler(handler: Handler?) {
        mMainActivityHandler = handler
    }

    fun removeMainActivityHandler() {
        mMainActivityHandler = null
    }

    private fun startForeground() {
        if (mIsNotificationVisible) {
            return
        }

        var type = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            type =
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE or ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        }

        ServiceCompat.startForeground(this, 9999, createNotification("Service started"), type)

        mIsNotificationVisible = true
    }

    private fun stopForeground() {
        if (mIsNotificationVisible) {
            ServiceCompat.stopForeground(this@WorkerService, ServiceCompat.STOP_FOREGROUND_REMOVE)
            mIsNotificationVisible = false
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val name: CharSequence = getString(R.string.notification_channel_name)
            val description = getString(R.string.notification_channel_description)

            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).also { it.description = description })
        }
    }

    private fun createNotification(value: String): Notification {
        return (NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_coin)
            .setContentTitle("Androdb Backgroung Worker")
            .setContentText(value)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_headup, "Stop", makePendingIntent(ACTION_STOP_SERVICE))
            .build())
    }

    private fun makePendingIntent(action: String): PendingIntent {
        val stopServiceIntent = Intent().apply {
            setAction(action)
        }

        return PendingIntent.getBroadcast(
            this,
            0,
            stopServiceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        @JvmField
        var isRunning: Boolean = false
        const val ACTION_STOP_SERVICE: String =
            "com.fr3tsOn.ecu.gui.androbd.action.STOP_WORKER_SERVICE"
        const val CHANNEL_ID = "com.fr3tsOn.ecu.gui.androbd.CHANNEL_ID"
        private const val TAG = "AndrOBD WorkerService"
    }
}