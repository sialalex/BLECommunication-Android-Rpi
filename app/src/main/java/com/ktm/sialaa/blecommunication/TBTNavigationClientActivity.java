package com.ktm.sialaa.blecommunication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashSet;

public class TBTNavigationClientActivity extends Activity {
    private BluetoothDevice bondedDevice;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Toast.makeText(getApplicationContext(), "Found Device! " + result.getDevice().getAddress(), Toast.LENGTH_SHORT).show();
            bondDevice(result.getDevice());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(getApplicationContext(), "Could not find a Device with the required Service!", Toast.LENGTH_LONG).show();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tbt_navigation_service);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString("71ced1ac-0000-44f5-9454-806ff70b3e02")).build();
        ScanSettings scanSettings = new ScanSettings.Builder().build();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(Collections.singletonList(scanFilter), scanSettings, scanCallback);
        //bluetoothLeScanner.startScan(scanCallback);
        Log.v("LE-Scan", "Started...");
    }

    private void bondDevice(BluetoothDevice device){
        if(device.getBondState() != BluetoothDevice.BOND_BONDED){
            device.createBond();
        }
        else{
            Toast.makeText(getApplicationContext(), "Already paired!", Toast.LENGTH_SHORT).show();
        }
        bluetoothLeScanner.stopScan(scanCallback);
        bondedDevice = device;

        //getServices();
    }

    private void getServices(){
        ParcelUuid[] deviceUUIDs = bondedDevice.getUuids();

        for (int i = 0; i < deviceUUIDs.length; i++) {
            Log.i("BLE-Service", deviceUUIDs[i].toString());
        }
    }
}
