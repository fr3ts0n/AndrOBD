package com.fr3ts0n.ecu.gui.androbd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.List;

@RequiresApi (api = Build.VERSION_CODES.LOLLIPOP)
public class BleDeviceListActivity
    extends BtDeviceListActivity
{
    private BluetoothLeScanner leScanner;

    @SuppressLint("InlinedApi")
    @Override
    protected void startDeviceScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            return;
        }
        leScanner = mBtAdapter.getBluetoothLeScanner();
        leScanner.startScan(scanCallback);
    }

    @SuppressLint("MissingPermission") // permission is checked before
    protected void stopDeviceScan() {
        if(leScanner != null)
            leScanner.stopScan(scanCallback);
    }

    final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            log.fine("BLE: " + result.toString());
            addDevice(result.getDevice());
        }
        @Override
        public void onScanFailed(int errorCode) {
            log.severe("BLE scan failed with error code: " + errorCode);
        }

        public void onBatchScanResults(List<ScanResult> results) {
            log.fine("BLE: " + results.toString());
            for (ScanResult result : results) {
                addDevice(result.getDevice());
            }
        }
    };
}
