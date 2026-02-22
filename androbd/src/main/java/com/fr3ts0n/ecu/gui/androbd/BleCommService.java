package com.fr3ts0n.ecu.gui.androbd;

import static java.util.logging.Level.SEVERE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.fr3ts0n.prot.ProtUtils;
import com.fr3ts0n.prot.TelegramListener;
import com.fr3ts0n.prot.TelegramWriter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleCommService
        extends CommService
        implements TelegramWriter, TelegramListener
{
    private static final UUID LE_CCCD           = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID LE_CC254X_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID LE_CC254X_CHAR_RW = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    private static final UUID LE_VLINK_SVC      = UUID.fromString("000018f0-0000-1000-8000-00805f9b34fb");
    private static final UUID LE_VLINK_RX       = UUID.fromString("00002af0-0000-1000-8000-00805f9b34fb");
    private static final UUID LE_VLINK_TX       = UUID.fromString("00002af1-0000-1000-8000-00805f9b34fb");

    private static final UUID LE_NEXAS          = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static final UUID LE_NEXAS_RX       = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static final UUID LE_NEXAS_TX       = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");

    private static final UUID LE_NRF_SERVICE    = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static final UUID LE_NRF_CHAR_RW2   = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"); // read on microbit, write on adafruit
    private static final UUID LE_NRF_CHAR_RW3   = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    private static final UUID LE_MICROCHIP_SERVICE    = UUID.fromString("49535343-FE7D-4AE5-8FA9-9FAFD205E455");
    private static final UUID LE_MICROCHIP_CHAR_RW    = UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616");
    private static final UUID LE_MICROCHIP_CHAR_W     = UUID.fromString("49535343-8841-43F4-A8D4-ECBE34729BB3");

    private static final UUID LE_TIO_SERVICE          = UUID.fromString("0000FEFB-0000-1000-8000-00805F9B34FB");
    private static final UUID LE_TIO_CHAR_TX          = UUID.fromString("00000001-0000-1000-8000-008025000000"); // WNR
    private static final UUID LE_TIO_CHAR_RX          = UUID.fromString("00000002-0000-1000-8000-008025000000"); // N
    private static final UUID LE_TIO_CHAR_TX_CREDITS  = UUID.fromString("00000003-0000-1000-8000-008025000000"); // W
    private static final UUID LE_TIO_CHAR_RX_CREDITS  = UUID.fromString("00000004-0000-1000-8000-008025000000"); // I

    /* Table of UUIDs for each service and corresponding RX/TX characteristics */
    private static final Map<UUID, UUID[]> serialUUIDs = Map.of(
            LE_NRF_SERVICE, new UUID[]{LE_NRF_CHAR_RW2, LE_NRF_CHAR_RW3},
            LE_NEXAS, new UUID[]{LE_NEXAS_RX, LE_NEXAS_TX},
            LE_VLINK_SVC, new UUID[]{LE_VLINK_RX, LE_VLINK_TX},
            LE_MICROCHIP_SERVICE, new UUID[]{LE_MICROCHIP_CHAR_RW, LE_MICROCHIP_CHAR_W},
            LE_CC254X_SERVICE, new UUID[]{LE_CC254X_CHAR_RW, LE_CC254X_CHAR_RW},
            LE_TIO_SERVICE, new UUID[]{LE_TIO_CHAR_TX, LE_TIO_CHAR_RX}
    );

    private BluetoothDevice mDevice;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic rxCharacteristic;
    private BluetoothGattCharacteristic txCharacteristic;

    private String message = "";

    BleCommService(Context context, Handler handler) {
        super(context, handler);
        // set up protocol handlers
        elm.addTelegramWriter(this);
    }

    @Override
    protected void start() {
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void stop() {
        gatt.disconnect();
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void write(byte[] out) {
        if(gatt != null && txCharacteristic != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeCharacteristic(txCharacteristic, out, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            } else {
                txCharacteristic.setValue(out);
                txCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                gatt.writeCharacteristic(txCharacteristic);
            }
        }
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public int writeTelegram(char[] buffer) {
        log.finer("TX: " + ProtUtils.hexDumpBuffer(buffer));
        String tgm = String.valueOf(buffer) + "\r";
        write(tgm.getBytes());
        return buffer.length;
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public int writeTelegram(char[] buffer, int type, Object id) {
        return writeTelegram(buffer);
    }

    @Override
    public int handleTelegram(char[] data) {
        int result = 0;

        log.finer("RX: " + ProtUtils.hexDumpBuffer(data));

        for(char chr : data)
        {
            switch (chr)
            {
                // ignore special characters
                case 32:
                    break;

                // trigger message handling for new request
                case '>':
                    //noinspection StringConcatenationInLoop
                    message += (char) chr;
                    // trigger message handling
                case 10:
                case 13:
                    try
                    {
                        if(!message.isEmpty()) {
                            result = elm.handleTelegram(message.toCharArray());
                        }
                    }
                    catch (Exception ex)
                    {
                        log.log(SEVERE,"handleTelegram", ex);
                    }
                    message = "";
                    break;

                default:
                    //noinspection StringConcatenationInLoop
                    message += (char) chr;
            }
        }
        return result;
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void connect(Object device, boolean secure) {
        mDevice = (BluetoothDevice)device;
        log.info("BLE connect to " + mDevice.getAddress() + " (" + mDevice.getName() + ")");
        setState(STATE.CONNECTING);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            gatt = mDevice.connectGatt(mContext, true, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            gatt = mDevice.connectGatt(mContext, true, bluetoothGattCallback);
        }
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt,
                                            int status,
                                            int newState) {
            log.fine(String.format("onConnectionStateChange: %s, %s", status, newState));
            switch(newState) {
                case  BluetoothProfile.STATE_CONNECTED:
                    gatt.discoverServices();
                    setState(STATE.CONNECTING);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    gatt.close();
                    connectionLost();
                    break;
            }
        }

        @SuppressLint("DefaultLocale")
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            log.fine(String.format("onServicesDiscovered: %d", status));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // reset remembered characteristics
                rxCharacteristic = txCharacteristic = null;
                List<BluetoothGattService> services;
                services = gatt.getServices();
                // find known serial service
                for (BluetoothGattService service : services) {
                    log.fine("GATT SVC:" + service.getUuid().toString());
                    if(!serialUUIDs.containsKey(service.getUuid()))
                        continue;
                    // found a service, check characteristics ...
                    UUID[] serCharUUIDs = serialUUIDs.get(service.getUuid());
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    // find corresponding RX & TX characteristics
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        log.fine("GATT CH:" + characteristic.getUuid().toString());
                        // RX
                        if (characteristic.getUuid().equals(serCharUUIDs[0])) {
                            log.info("GATT RX:" + characteristic.getUuid().toString());
                            rxCharacteristic = characteristic;
                            // register characteristic for RX notification
                            gatt.setCharacteristicNotification(characteristic, true);
                            BluetoothGattDescriptor desc = characteristic.getDescriptor(LE_CCCD);
                            if(desc != null) {
                                desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(desc);
                            }
                        }
                        // TX
                        if (characteristic.getUuid().equals(serCharUUIDs[1])){
                            log.info("GATT TX:" + characteristic.getUuid().toString());
                            // save characteristic for TX
                            txCharacteristic = characteristic;
                        }
                    }
                    // RX & TX connected? > connection established
                    if (rxCharacteristic != null && txCharacteristic != null) {
                        // notify connection established
                        connectionEstablished(mDevice.getName());
                        // cancel service loop
                        return;
                    }
                }
                // nothing matching found > connection failed
                connectionFailed();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            /* Handle incoming characteristic changes
             * Since only RX characteristic is registered for notification, we handle RX events here */
            byte[] newValue = characteristic.getValue();
            handleTelegram(new String(newValue).toCharArray());
        }
    };
}
