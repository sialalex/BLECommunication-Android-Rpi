/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ktm.sialaa.blecommunication;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;


public class TBTNavigationServiceFragment extends ServiceFragment {

    //TBT Navigation Service UUID
    private static final UUID TBT_NAVIGATION_SERVICE_UUID = UUID
            .fromString("71ced1ac-0000-44f5-9454-806ff70b3e02");

    //Navigation-State Characateristic & Descriptor
    private static final UUID CHARACT_NAVSTATE_UUID = UUID
            .fromString("71ced1ac-0003-44f5-9454-806ff70b3e02");
    private static final String CHARACT_NAVSTATE_DESC = "Navigation-State";

    //Turn-Icon Characateristic & Descriptor
    private static final UUID CHARACT_TURNICON_UUID = UUID
            .fromString("71ced1ac-0004-44f5-9454-806ff70b3e02");
    private static final String CHARACT_TURNICON_DESC = "Turn-Icon";

    //Turn-Icon Characateristic & Descriptor
    private static final UUID CHARACT_TURNDISTANCE_UUID = UUID
            .fromString("71ced1ac-0005-44f5-9454-806ff70b3e02");
    private static final String CHARACT_TURNDISTANCE_DESC = "Turn-Distance";

    //Turn-Info Characateristic & Descriptor
    private static final UUID CHARACT_TURNINFO_UUID = UUID
            .fromString("71ced1ac-0006-44f5-9454-806ff70b3e02");
    private static final String CHARACT_TURNINFO_DESC = "Turn-Info";

    //Turn-Road Characateristic & Descriptor
    private static final UUID CHARACT_TURNROAD_UUID = UUID
            .fromString("71ced1ac-0007-44f5-9454-806ff70b3e02");
    private static final String CHARACT_TURNROAD_DESC = "Turn-Road";

    //Service Delegate
    private ServiceFragmentDelegate mDelegate;

    //UI
    Button updateServicebtn;
    View view;


    //GATT Characteristics
    private BluetoothGattService tbtNavigationService;
    private BluetoothGattCharacteristic navStateCharacteristic;
    private BluetoothGattCharacteristic turnIconCharacteristic;
    private BluetoothGattCharacteristic turnDistanceCharacteristic;
    private BluetoothGattCharacteristic turnInfoCharacteristic;
    private BluetoothGattCharacteristic turnRoadCharacteristic;

    //GATT Characteristics Values
    byte visibility = 0;
    byte[] navStateCharacteristic_value = new byte[32];
    byte[] turnIconCharacteristic_value = new byte[32];
    byte[] turnDistanceCharacteristic_value = new byte[32];
    byte[] turnInfoCharacteristic_value = new byte[48];
    byte[] turnRoadCharacteristic_value = new byte[64];

    private final OnClickListener mNotifyButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mDelegate.sendNotificationToDevices(navStateCharacteristic);
            mDelegate.sendNotificationToDevices(turnIconCharacteristic);
            mDelegate.sendNotificationToDevices(turnDistanceCharacteristic);
            mDelegate.sendNotificationToDevices(turnInfoCharacteristic);
            mDelegate.sendNotificationToDevices(turnRoadCharacteristic);
        }
    };


    public TBTNavigationServiceFragment() {
        tbtNavigationService = new BluetoothGattService(TBT_NAVIGATION_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        //Setup and add the nav-state characteristic
        setupNavStateCharacteristic();
        tbtNavigationService.addCharacteristic(navStateCharacteristic);

        //Setup and add the turn-icon characteristic
        setupTurnIconCharacteristic();
        tbtNavigationService.addCharacteristic(turnIconCharacteristic);

        //Setup and add the turn-icon characteristic
        setupTurnDistanceCharacteristic();
        tbtNavigationService.addCharacteristic(turnDistanceCharacteristic);

        //Setup and add the turn-info characteristic
        setupTurnInfoCharacteristic();
        tbtNavigationService.addCharacteristic(turnInfoCharacteristic);

        //Setup and add the turn-road characteristic
        setupTurnRoadCharacteristic();
        tbtNavigationService.addCharacteristic(turnRoadCharacteristic);

    }

    //Setup the Navigation State Characteristic
    private void setupNavStateCharacteristic() {
        navStateCharacteristic =
                new BluetoothGattCharacteristic(CHARACT_NAVSTATE_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        navStateCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        navStateCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(CHARACT_NAVSTATE_DESC));

        navStateCharacteristic.setValue(navStateCharacteristic_value);
    }

    //Setup the Turn Icon Characteristic
    private void setupTurnIconCharacteristic() {
        turnIconCharacteristic =
                new BluetoothGattCharacteristic(CHARACT_TURNICON_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        turnIconCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        turnIconCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(CHARACT_TURNICON_DESC));

        turnIconCharacteristic.setValue(turnIconCharacteristic_value);
    }

    //Setup the Turn Distance Characteristic
    private void setupTurnDistanceCharacteristic() {
        turnDistanceCharacteristic =
                new BluetoothGattCharacteristic(CHARACT_TURNDISTANCE_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        turnDistanceCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        turnDistanceCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(CHARACT_TURNDISTANCE_DESC));

        turnDistanceCharacteristic.setValue(turnDistanceCharacteristic_value);
    }

    //Setup the Turn Distance Characteristic
    private void setupTurnInfoCharacteristic() {
        turnInfoCharacteristic =
                new BluetoothGattCharacteristic(CHARACT_TURNINFO_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        turnInfoCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        turnInfoCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(CHARACT_TURNINFO_DESC));

        turnInfoCharacteristic.setValue(turnInfoCharacteristic_value);
    }

    //Setup the Turn Distance Characteristic
    private void setupTurnRoadCharacteristic() {
        turnRoadCharacteristic =
                new BluetoothGattCharacteristic(CHARACT_TURNROAD_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        turnRoadCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        turnRoadCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(CHARACT_TURNROAD_DESC));

        turnRoadCharacteristic.setValue(turnRoadCharacteristic_value);
    }

    // Lifecycle callbacks
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_tbt_navigation_service, container, false);

        updateServicebtn = (Button) view.findViewById(R.id.updateServiceBtn);
        setupBtnListener();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDelegate = (ServiceFragmentDelegate) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ServiceFragmentDelegate");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDelegate = null;
    }

    public BluetoothGattService getBluetoothGattService() {
        return tbtNavigationService;
    }

    @Override
    public ParcelUuid getServiceUUID() {
        return new ParcelUuid(TBT_NAVIGATION_SERVICE_UUID);
    }


    @Override
    public void notificationsEnabled(BluetoothGattCharacteristic characteristic, boolean indicate) {
        if (characteristic.getUuid() != CHARACT_TURNICON_UUID) {
            return;
        }
        if (indicate) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), R.string.notificationsEnabled, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void notificationsDisabled(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid() != CHARACT_TURNICON_UUID) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), R.string.notificationsNotEnabled, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public int writeCharacteristic(BluetoothGattCharacteristic characteristic, int offset, byte[] value) {
        return BluetoothGatt.GATT_SUCCESS;
    }

    private void setupBtnListener() {
        updateServicebtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Update", "Updating the service....");

                updateTbtNavigationService();
            }
        });
    }

    private void updateTbtNavigationService() {
        updateNavStateCharacteristic();
        updateTurnIconCharacteristic();
        updateTurnDistanceCharacteristic();
    }

    private void updateNavStateCharacteristic() {
        boolean navigationState = ((Switch) view.findViewById(R.id.navigationStateSwitch)).isChecked();
        boolean gpsIcon = ((Switch) view.findViewById(R.id.gpsIconSwitch)).isChecked();

        //Get the byte 17 to modify it
        byte byte17 = navStateCharacteristic_value[16];

        //Check the navigation state and update the first bit (0)
        if(navigationState){ //navigation is activated
            byte17 = (byte) (byte17 | (1 << 0));
        }else{ //navigation is not activated
            byte17 = (byte) (byte17 & ~(1 << 0));
        }

        //Check the gps icon state and update the second bit (1)
        if(gpsIcon){ //gps icon is on
            byte17 = (byte) (byte17 | (1 << 1));
        }else{ //gps icon is off
            byte17 = (byte) (byte17 & ~(1 << 1));
        }

        //Update the byte 17 and set the whole byte-array
        navStateCharacteristic_value[16] = byte17;
        navStateCharacteristic.setValue(navStateCharacteristic_value);

        for(int i = 0; i < navStateCharacteristic_value.length; i++){
            Log.i("Byte", i + ": " + navStateCharacteristic_value[i]);
        }
    }

    private void updateTurnIconCharacteristic(){
        //Convert the entered number to hex
        String editTextValue = ((EditText) view.findViewById(R.id.turnIconEditText)).getText().toString();
        String turnIcon = "";
        try{
            turnIcon = Integer.toHexString(Integer.parseInt(editTextValue));
        }catch (Exception ex){
            ex.printStackTrace();
        }

        //Set visibility and the turnIcon
        turnIconCharacteristic_value[16] = visibility;
        turnIconCharacteristic_value[17] = Byte.parseByte(turnIcon);
        turnIconCharacteristic.setValue(turnIconCharacteristic_value);
    }

    private void updateTurnDistanceCharacteristic(){
        //Get the value of the edit-text and convert it to a byte array
        String editTextValue = ((EditText) view.findViewById(R.id.turnDistanceEditText)).getText().toString();
        byte[] turnDistance = editTextValue.getBytes();

        //Check if the entered distance is too long or too short -> modify it
        if(turnDistance.length > 8){
            turnDistance = Arrays.copyOfRange(turnDistance, 0, 7);
        }
        if(turnDistance.length < 8){
            for(int i = turnDistance.length; i < 8; i++){
                turnDistance[i] = 0;
            }
        }

        //Fill the characteristic array with the 8 bytes (from 17 to 24)
        int i1 = 0;
        for(int i2 = 17; i2 == 24; i2++){
            turnIconCharacteristic_value[i2] = turnDistance[i1];
            i1++;
        }

        //Set the visibility (byte 17) and set the value
        turnDistanceCharacteristic_value[16] = visibility;
        turnIconCharacteristic.setValue(turnDistanceCharacteristic_value);
    }

}
