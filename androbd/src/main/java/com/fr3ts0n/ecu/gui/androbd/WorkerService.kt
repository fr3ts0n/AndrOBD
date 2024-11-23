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
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.fr3ts0n.ecu.gui.androbd.CommService.STATE
import com.fr3ts0n.ecu.prot.obd.Messages
import com.fr3ts0n.ecu.prot.obd.ObdProt
import com.fr3ts0n.pvs.PvChangeEvent
import com.fr3ts0n.pvs.PvChangeListener
import com.fr3ts0n.pvs.PvList

class WorkerService : Service() {
    private val binder = WorkerServiceBinder()
    private val receiver = Receiver()
    private var mCommService: CommService? = null
    var mMainActivityHandler: Handler? = null
    private var isNotificationVisible = false
    private var mLastBluetoothDeviceAddress = ""
    private var mIsCommServiceConnected = false
    private var mCommServiceState: CommService.STATE? = null

    private var mHandler: Handler? = null
    private val mPvListListeners: ArrayList<PvListChangeListener> = ArrayList()
    private var mMessageQueue: HashMap<Int, Message> = HashMap()

    inner class WorkerServiceBinder : Binder() {
        val service: WorkerService
            get() = this@WorkerService
    }

    inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(ACTION_STOP_SERVICE)) {
                this@WorkerService.stop()
            }
        }
    }

    private inner class PvListChangeListener(val mAdapter: ObdItemAdapter, val mPvList: PvList) :
        PvChangeListener {
        init {
            mPvList.addPvChangeListener(this)
        }

        override fun pvChanged(event: PvChangeEvent) {
            mAdapter.setPvList(mAdapter.pvs)
        }

        fun unsubscribeFromPvList() {
            mPvList.removePvChangeListener(this)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        unsubscribeFromPvListChanges();
        subscribeToPvListChanges();

        mMessageQueue.apply {
            forEach {
                mMainActivityHandler?.obtainMessage(it.key, it.value.obj)?.apply {
                    data = it.value.data
                    sendToTarget()
                }
            }
            clear()
        }

        return binder
    }

    override fun onCreate() {
        isRunning = true

        createNotificationChannel()

        subscribeToPvListChanges()

        val intentFilter = IntentFilter(ACTION_STOP_SERVICE)
        val receiverFlags = ContextCompat.RECEIVER_NOT_EXPORTED

        ContextCompat.registerReceiver(this, receiver, intentFilter, receiverFlags)
    }

    override fun onDestroy() {
        isRunning = false

        unsubscribeFromPvListChanges()

        if (isNotificationVisible) {
            stopForeground()
        }

        stopCommService()

        unregisterReceiver(receiver)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mHandler = object : Handler(mainLooper) {
            override fun handleMessage(msg: Message) {
                if (msg.what == MainActivity.MESSAGE_STATE_CHANGE) {
                    val msgState = msg.obj as STATE
                    if (msgState == STATE.CONNECTED ||
                        msgState == STATE.CONNECTING
                    ) {
                        this@WorkerService.startForeground()
                        this@WorkerService.isNotificationVisible = true
                    } else {
                        if (this@WorkerService.isNotificationVisible) {
                            this@WorkerService.stopForeground()
                            this@WorkerService.isNotificationVisible = false
                        }

                        this@WorkerService.mIsCommServiceConnected = false
                        this@WorkerService.mLastBluetoothDeviceAddress = ""
                        this@WorkerService.mMessageQueue.clear()
                    }

                    if (msgState == STATE.CONNECTED) {
                        this@WorkerService.mIsCommServiceConnected = true
                    }

                    this@WorkerService.mCommServiceState = msgState
                }

                val requiredStateTypes = arrayOf(
                    MainActivity.MESSAGE_STATE_CHANGE,
                    MainActivity.MESSAGE_DEVICE_NAME,
                    MainActivity.MESSAGE_DATA_ITEMS_CHANGED,
                    MainActivity.MESSAGE_TOAST
                )

                if (msg.what in requiredStateTypes) {
                    this@WorkerService.mMessageQueue[msg.what] = msg
                }

                this@WorkerService.mMainActivityHandler?.obtainMessage(msg.what, msg.obj)?.apply {
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
        if (isNotificationVisible) {
            stopForeground()
        }

        stopSelf()
    }

    fun requestCurrentCommServiceState() {
        mCommServiceState?.let {
            mMainActivityHandler
                ?.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, it)
                ?.sendToTarget()
        }
    }

    fun connectBT(device: BluetoothDevice, isSecure: Boolean) {
        if (mIsCommServiceConnected && mLastBluetoothDeviceAddress == device.address && mMainActivityHandler != null) {
            return
        }

        mCommService = BtCommService(this, mHandler).also {
            stopCommService()
            mLastBluetoothDeviceAddress = device.address
            it.connect(device, isSecure)
        }
    }


    fun connectDeviceUsb() {
        mCommService = UsbCommService(this, mHandler).also {
            stopCommService()
            it.connect(UsbDeviceListActivity.selectedPort, true)
        }
    }

    fun connectNetworkDevice(address: String?, port: Int) {
        mCommService = NetworkCommService(this, mHandler).also {
            stopCommService()
            it.connect(address, port)
        }
    }

    private fun stopCommService() {
        mCommService?.let {
            it.stop()
            mCommService = null
            mIsCommServiceConnected = false
        }
    }

    private fun subscribeToPvListChanges() {
        mPvListListeners.apply {
            add(PvListChangeListener(MainActivity.mPidAdapter, ObdProt.PidPvs))
            add(PvListChangeListener(MainActivity.mVidAdapter, ObdProt.VidPvs))
            add(PvListChangeListener(MainActivity.mTidAdapter, ObdProt.VidPvs))
            add(PvListChangeListener(MainActivity.mDfcAdapter, ObdProt.tCodes))
            add(PvListChangeListener(MainActivity.mPluginDataAdapter, MainActivity.mPluginPvs))
        }
    }

    private fun unsubscribeFromPvListChanges() {
        mPvListListeners.apply {
            forEach { it.unsubscribeFromPvList() }
            clear()
        }
    }

    fun setMainActivityHandler(handler: Handler?) {
        mMainActivityHandler = handler
    }

    fun removeMainActivityHandler() {
        mMainActivityHandler = null
    }

    private fun startForeground() {
        if (isNotificationVisible) {
            return
        }

        var type = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            type =
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE or ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        }

        ServiceCompat.startForeground(this, 9999, createNotification("Service started"), type)

        isNotificationVisible = true
    }

    private fun stopForeground() {
        if (isNotificationVisible) {
            ServiceCompat.stopForeground(this@WorkerService, ServiceCompat.STOP_FOREGROUND_REMOVE)
            isNotificationVisible = false
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
    }
}