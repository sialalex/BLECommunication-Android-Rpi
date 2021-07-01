package com.ktm.sialaa.blecommunication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TBTNavigationClientActivity extends Activity {
    //GUI Elements
    TextView stateTextView;
    TextView servicesTextView;


    //Bluetooth LE Elements
    private BluetoothDevice bluetoothDevice;
    private String bluetoothDeviceName;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    //Handles the events of the LE-Scanner
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        //The first device which is found (with the specific service) is used
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //Select the device and the name
            bluetoothDeviceName = result.getScanRecord().getDeviceName();
            bluetoothDevice = result.getDevice();
            //Inform the user
            Toast.makeText(getApplicationContext(), "Found Device! " + result.getDevice().getAddress(), Toast.LENGTH_SHORT).show();
            stateTextView.setText("Connecting with " + bluetoothDeviceName + "...");

            //Stop the scan and connect with the device
            bluetoothLeScanner.stopScan(scanCallback);
            BluetoothGatt gatt = bluetoothDevice.connectGatt(getApplicationContext(), false, bluetoothGattCallback);
        }

        //This message will be showed when no device is found
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(getApplicationContext(), "Could not find a Device with the required Service!", Toast.LENGTH_LONG).show();
            stateTextView.setText("No Device with the required Service found!");
        }

    };

    //Handles the events of the Gatt-Object
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            //If the device is connected, start discovering all services
            if (BluetoothGatt.STATE_CONNECTED == newState) {
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.i("BluetoohtLE", "BLE-Services discovered!");

            //When services were found, they get printed out for the user
            printServices(gatt);

            writeRCMSCharacteristic(gatt);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tbt_navigation_client);

        //Setup the button listener
        selectGUIElements();

        //Get the ble-manager and the ble-adapter
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        startBLEScan();
    }

    private void selectGUIElements() {
        //TextView for state infos
       stateTextView = findViewById(R.id.stateTextView);

       //TextView for the services
        servicesTextView = findViewById(R.id.servicesTextView);

    }

    private void startBLEScan(){
        //Set the scan-filter and the scan-settings
        //Just looking for devices, which advertise a specific service
        ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString("71ced1ac-abcd-44f5-9454-806ff70b3e02")).build();
        ScanSettings scanSettings = new ScanSettings.Builder().build();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(Collections.singletonList(scanFilter), scanSettings, scanCallback);
        Log.v("BluetoothLE", "Started BLE-Scan...");
    }

    private void printServices(BluetoothGatt gatt){
        final List<BluetoothGattService> services = gatt.getServices();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String serviceText = "";
                for (BluetoothGattService service : services) {
                    serviceText = serviceText + "" + service.getUuid() + "\n";
                }
                servicesTextView.setText(serviceText);

            }
        });

    }

    private void writeRCMSCharacteristic(BluetoothGatt gatt){
        byte[] newValue = new byte[32];
        for (int i = 0; i < newValue.length; i++) {
            newValue[i] = (byte) i;
        }

        BluetoothGattCharacteristic remoteControlModeService = gatt.getService(UUID.fromString("71ced1ac-0100-44f5-9454-806ff70b3e02")).getCharacteristic(UUID.fromString("71ced1ac-0103-44f5-9454-806ff70b3e02"));
        remoteControlModeService.setValue(newValue);
        boolean a = gatt.writeCharacteristic(remoteControlModeService);

        Log.i("BluetoothLe", "RCMS Characteristic written");
    }

}
