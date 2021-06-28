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
import android.widget.Spinner;
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

    //Estimated Arrival Time Characteristic & Descriptor
    private static final UUID CHARACT_ESTARRIVALTIME_UUID = UUID
            .fromString("71ced1ac-0008-44f5-9454-806ff70b3e02");
    private static final String CHARACT_ESTARRIVALTIME_DESC = "Estimated Arrival Time";

    //Distance to Destination Characteristic & Descriptor
    private static final UUID CHARACT_DIST2DEST_UUID = UUID
            .fromString("71ced1ac-0009-44f5-9454-806ff70b3e02");
    private static final String CHARACT_DIST2DEST_DESC = "Distance to Destination";

    //Notification Characteristic & Descriptor
    private static final UUID CHARACT_NOTIFICATION_UUID = UUID
            .fromString("71ced1ac-000a-44f5-9454-806ff70b3e02");
    private static final String CHARACT_NOTIFICATION_DESC = "Notification";

    //Navigation Command Characteristic & Descriptor
    private static final UUID CHARACT_NAVIGATIONCOMMAND_UUID = UUID
            .fromString("71ced1ac-000b-44f5-9454-806ff70b3e02");
    private static final String CHARACT_NAVIGATIONCOMMAND_DESC = "Navigation Command";

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
    private BluetoothGattCharacteristic estArrivalTimeCharacteristic;
    private BluetoothGattCharacteristic dist2DestCharacteristic;
    private BluetoothGattCharacteristic notificationCharacteristic;
    private BluetoothGattCharacteristic navigationCommandCharacteristic;


    //GATT Characteristics Values
    byte visibility = 0;
    byte[] navStateCharacteristic_value = new byte[32];
    byte[] turnIconCharacteristic_value = new byte[32];
    byte[] turnDistanceCharacteristic_value = new byte[32];
    byte[] turnInfoCharacteristic_value = new byte[48];
    byte[] turnRoadCharacteristic_value = new byte[64];
    byte[] estArrivalTimeCharacteristic_value = new byte[32];
    byte[] dist2DestCharacteristic_value = new byte[32];
    byte[] notificationCharacteristic_value = new byte[48];
    byte[] navigationCommandCharacteristic_value = new byte[32];

    private final OnClickListener mNotifyButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mDelegate.sendNotificationToDevices(navStateCharacteristic);
            mDelegate.sendNotificationToDevices(turnIconCharacteristic);
            mDelegate.sendNotificationToDevices(turnDistanceCharacteristic);
            mDelegate.sendNotificationToDevices(turnInfoCharacteristic);
            mDelegate.sendNotificationToDevices(turnRoadCharacteristic);
            mDelegate.sendNotificationToDevices(estArrivalTimeCharacteristic);
            mDelegate.sendNotificationToDevices(dist2DestCharacteristic);
            mDelegate.sendNotificationToDevices(notificationCharacteristic);
            mDelegate.sendNotificationToDevices(navStateCharacteristic);
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

        //Setup and add the estimated arrival time characteristic
        setupEstArrivalTimeCharacteristic();
        tbtNavigationService.addCharacteristic(estArrivalTimeCharacteristic);

        //Setup and add the distance to destincation characteristic
        setupDist2DestCharacteristic();
        tbtNavigationService.addCharacteristic(dist2DestCharacteristic);

        //Setup and add the notification characteristic
        setupNotificationCharacteristic();
        tbtNavigationService.addCharacteristic(notificationCharacteristic);

        //Setup and add the navigation command characteristic
        setupNavigationCommandCharacteristic();
        tbtNavigationService.addCharacteristic(navigationCommandCharacteristic);
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

    //Setup the Turn Info Characteristic
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

    //Setup the Turn Road Characteristic
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

    //Setup the Estimated Arrival Time Characteristic
    private void setupEstArrivalTimeCharacteristic() {
        estArrivalTimeCharacteristic =
                new BluetoothGattCharacteristic(CHARACT_ESTARRIVALTIME_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        estArrivalTimeCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        estArrivalTimeCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(CHARACT_ESTARRIVALTIME_DESC));

        estArrivalTimeCharacteristic.setValue(estArrivalTimeCharacteristic_value);
    }

    //Setup the Distance to Destination Characteristic
    private void setupDist2DestCharacteristic() {
        dist2DestCharacteristic =
                new BluetoothGattCharacteristic(CHARACT_DIST2DEST_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        dist2DestCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        dist2DestCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(CHARACT_DIST2DEST_DESC));

        dist2DestCharacteristic.setValue(dist2DestCharacteristic_value);
    }

    //Setup the Notification Characteristic
    private void setupNotificationCharacteristic() {
        notificationCharacteristic =
                new BluetoothGattCharacteristic(CHARACT_NOTIFICATION_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        notificationCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        notificationCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(CHARACT_NOTIFICATION_DESC));

        notificationCharacteristic.setValue(notificationCharacteristic_value);
    }

    //Setup the Navigation Command Characteristic
    private void setupNavigationCommandCharacteristic() {
        navigationCommandCharacteristic =
                new BluetoothGattCharacteristic(CHARACT_NAVIGATIONCOMMAND_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        navigationCommandCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        navigationCommandCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(CHARACT_NAVIGATIONCOMMAND_DESC));

        navigationCommandCharacteristic.setValue(navigationCommandCharacteristic_value);
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

    //This methods selects the values (changed by the user) and updates each characteristic
    private void updateTbtNavigationService() {
        updateNavStateCharacteristic();
        updateTurnIconCharacteristic();
        updateTurnDistanceCharacteristic();
        updateTurnInfoCharacteristic();
        updateTurnRoadCharacteristic();
        updateEstArrivalTimeCharacteristic();
        updateDist2DestCharacteristic();
        updateNotificationCharacteristic();
        updateNavigationCommandCharacteristic();
    }

    private void updateNavStateCharacteristic() {
        boolean navigationState = ((Switch) view.findViewById(R.id.navigationStateSwitch)).isChecked();
        boolean gpsIcon = ((Switch) view.findViewById(R.id.gpsIconSwitch)).isChecked();

        //Get the byte 17 to modify it
        byte byte17 = navStateCharacteristic_value[16];

        //Check the navigation state and update the first bit (0)
        if (navigationState) { //navigation is activated
            byte17 = (byte) (byte17 | (1 << 0));
        } else { //navigation is not activated
            byte17 = (byte) (byte17 & ~(1 << 0));
        }

        //Check the gps icon state and update the second bit (1)
        if (gpsIcon) { //gps icon is on
            byte17 = (byte) (byte17 | (1 << 1));
        } else { //gps icon is off
            byte17 = (byte) (byte17 & ~(1 << 1));
        }

        //Update the byte 17 and set the whole byte-array
        navStateCharacteristic_value[16] = byte17;
        navStateCharacteristic.setValue(navStateCharacteristic_value);

        for (int i = 0; i < navStateCharacteristic_value.length; i++) {
            Log.i("Byte", i + ": " + navStateCharacteristic_value[i]);
        }
    }

    private void updateTurnIconCharacteristic() {
        //Convert the entered number to hex
        String editTextValue = ((EditText) view.findViewById(R.id.turnIconEditText)).getText().toString();
        String turnIcon = "";
        try {
            turnIcon = Integer.toHexString(Integer.parseInt(editTextValue));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Set visibility and the turnIcon
        turnIconCharacteristic_value[16] = visibility;
        turnIconCharacteristic_value[17] = Byte.parseByte(turnIcon);
        turnIconCharacteristic.setValue(turnIconCharacteristic_value);
    }

    private void updateTurnDistanceCharacteristic() {
        //Get the value of the edit-text and convert it to a byte array
        String editTextValue = ((EditText) view.findViewById(R.id.turnDistanceEditText)).getText().toString();
        byte[] turnDistance = editTextValue.getBytes();

        //Check if the entered distance is too long or too short -> modify it
        if (turnDistance.length > 8) {
            turnDistance = Arrays.copyOfRange(turnDistance, 0, 7);
        }
        if (turnDistance.length < 8) {
            byte[] turnDistance_new = new byte[8];

            for (int i = 0; i < turnDistance.length; i++) {
                turnDistance_new[i] = turnDistance[i];
            }

            for (int i = turnDistance.length; i < 8; i++) {
                turnDistance_new[i] = 0;
            }

            turnDistance = turnDistance_new;
        }

        //Fill the characteristic array with the 8 bytes (from 17 to 24)
        int i1 = 0;
        for (int i2 = 17; i2 <= 24; i2++) {
            turnDistanceCharacteristic_value[i2] = turnDistance[i1];
            i1++;
        }

        //Set the visibility (byte 17) and set the value
        turnDistanceCharacteristic_value[16] = visibility;
        turnDistanceCharacteristic.setValue(turnDistanceCharacteristic_value);
    }

    private void updateTurnInfoCharacteristic() {
        //Get the value of the edit-text and convert it to a byte array
        String editTextValue = ((EditText) view.findViewById(R.id.turnInfoEditText)).getText().toString();
        byte[] turnInfo = editTextValue.getBytes();

        //Check if the entered distance is too long or too short -> modify it
        if (turnInfo.length > 16) {
            turnInfo = Arrays.copyOfRange(turnInfo, 0, 15);
        }
        if (turnInfo.length < 16) {
            byte[] turnInfo_new = new byte[16];

            for (int i = 0; i < turnInfo.length; i++) {
                turnInfo_new[i] = turnInfo[i];
            }

            for (int i = turnInfo.length; i < 16; i++) {
                turnInfo_new[i] = 0;
            }

            turnInfo = turnInfo_new;
        }

        //Fill the characteristic array with the 8 bytes (from 17 to 32)
        int i1 = 0;
        for (int i2 = 17; i2 <= 32; i2++) {
            turnInfoCharacteristic_value[i2] = turnInfo[i1];
            i1++;
        }

        //Set the visibility (byte 17) and set the value
        turnInfoCharacteristic_value[16] = visibility;
        turnInfoCharacteristic.setValue(turnInfoCharacteristic_value);
    }

    private void updateTurnRoadCharacteristic() {
        //Get the value of the edit-text and convert it to a byte array
        String editTextValue = ((EditText) view.findViewById(R.id.turnRoadEditText)).getText().toString();
        byte[] turnRoad = editTextValue.getBytes();

        //Check if the entered distance is too long or too short -> modify it
        if (turnRoad.length > 32) {
            turnRoad = Arrays.copyOfRange(turnRoad, 0, 31);
            turnRoad[29] = Byte.parseByte(".");
            turnRoad[30] = Byte.parseByte(".");
            turnRoad[31] = Byte.parseByte(".");
        }
        if (turnRoad.length < 32) {
            byte[] turnRoad_new = new byte[32];

            for (int i = 0; i < turnRoad.length; i++) {
                turnRoad_new[i] = turnRoad[i];
            }

            for (int i = turnRoad.length; i < 32; i++) {
                turnRoad_new[i] = 0;
            }

            turnRoad = turnRoad_new;
        }

        //Fill the characteristic array with the 8 bytes (from 17 to 48)
        int i1 = 0;
        for (int i2 = 17; i2 <= 48; i2++) {
            turnRoadCharacteristic_value[i2] = turnRoad[i1];
            i1++;
        }

        //Set the visibility (byte 17) and set the value
        turnRoadCharacteristic_value[16] = visibility;
        turnRoadCharacteristic.setValue(turnRoadCharacteristic_value);
    }

    private void updateEstArrivalTimeCharacteristic() {
        //Get the value of the edit-text and convert it to a byte array
        String editTextValue = ((EditText) view.findViewById(R.id.estArrivalTimeEditText)).getText().toString();
        byte[] estArrivalTime = editTextValue.getBytes();

        //Check if the entered distance is too long or too short -> modify it
        if (estArrivalTime.length > 8) {
            estArrivalTime = Arrays.copyOfRange(estArrivalTime, 0, 7);
        }
        if (estArrivalTime.length < 8) {
            byte[] estArrivalTime_new = new byte[8];

            for (int i = 0; i < estArrivalTime.length; i++) {
                estArrivalTime_new[i] = estArrivalTime[i];
            }

            for (int i = estArrivalTime.length; i < 8; i++) {
                estArrivalTime_new[i] = 0;
            }

            estArrivalTime = estArrivalTime_new;
        }

        //Fill the characteristic array with the 8 bytes (from 17 to 24)
        int i1 = 0;
        for (int i2 = 17; i2 <= 24; i2++) {
            estArrivalTimeCharacteristic_value[i2] = estArrivalTime[i1];
            i1++;
        }

        //Set the visibility (byte 17) and set the value
        estArrivalTimeCharacteristic_value[16] = visibility;
        estArrivalTimeCharacteristic.setValue(estArrivalTimeCharacteristic_value);
    }

    private void updateDist2DestCharacteristic() {
        //Get the value of the edit-text and convert it to a byte array
        String editTextValue = ((EditText) view.findViewById(R.id.dist2DestEditText)).getText().toString();
        byte[] dist2Dest = editTextValue.getBytes();

        //Check if the entered distance is too long or too short -> modify it
        if (dist2Dest.length > 8) {
            dist2Dest = Arrays.copyOfRange(dist2Dest, 0, 7);
        }
        if (dist2Dest.length < 8) {
            byte[] dist2Dest_new = new byte[8];

            for (int i = 0; i < dist2Dest.length; i++) {
                dist2Dest_new[i] = dist2Dest[i];
            }

            for (int i = dist2Dest.length; i < 8; i++) {
                dist2Dest_new[i] = 0;
            }

            dist2Dest = dist2Dest_new;
        }

        //Fill the characteristic array with the 8 bytes (from 17 to 24)
        int i1 = 0;
        for (int i2 = 17; i2 <= 24; i2++) {
            dist2DestCharacteristic_value[i2] = dist2Dest[i1];
            i1++;
        }

        //Set the visibility (byte 17) and set the value
        dist2DestCharacteristic_value[16] = visibility;
        dist2DestCharacteristic.setValue(dist2DestCharacteristic_value);
    }

    private void updateNotificationCharacteristic() {
        //Get the selected icon (of the spinner) and convert it to hex byte
        String spinnerValue = ((Spinner) view.findViewById(R.id.notificationSpinner)).getSelectedItem().toString();
        byte notificationIcon = 0;
        switch (spinnerValue) {
            case "Rerouting":
                notificationIcon = Byte.parseByte(Integer.toHexString(1));
                break;
            case "Waypoint":
                notificationIcon = Byte.parseByte(Integer.toHexString(2));
                break;
        }
        //Set the notification icon value on byte 17
        notificationCharacteristic_value[17] = notificationIcon;


        //Get the value of the edit-text and convert it to a byte array
        String editTextValue = ((EditText) view.findViewById(R.id.notificationEditText)).getText().toString();
        byte[] notificationText = editTextValue.getBytes();

        //Check if the entered distance is too long or too short -> modify it
        if (notificationText.length > 16) {
            notificationText = Arrays.copyOfRange(notificationText, 0, 15);
        }
        if (notificationText.length < 16) {
            byte[] notificationText_new = new byte[16];

            for (int i = 0; i < notificationText.length; i++) {
                notificationText_new[i] = notificationText[i];
            }

            for (int i = notificationText.length; i < 16; i++) {
                notificationText_new[i] = 0;
            }

            notificationText = notificationText_new;
        }

        //Fill the characteristic array with the 8 bytes (from 17 to 32)
        int i1 = 0;
        for (int i2 = 18; i2 <= 33; i2++) {
            notificationCharacteristic_value[i2] = notificationText[i1];
            i1++;
        }

        //Set the visibility (byte 17) and set the value
        notificationCharacteristic_value[16] = visibility;
        notificationCharacteristic.setValue(notificationCharacteristic_value);
    }

    private void updateNavigationCommandCharacteristic() {
        String spinnerValue = ((Spinner) view.findViewById(R.id.navigationCommandSpinner)).getSelectedItem().toString();
        int spinnerIndex = (int) ((Spinner) view.findViewById(R.id.navigationCommandSpinner)).getSelectedItemId();

        byte byte16 = 0;
        switch (spinnerIndex) {
            case 0:
                byte16 = Byte.parseByte("00000000", 2);
                break; //0x00
            case 1:
                byte16 = Byte.parseByte("00000001", 2);
                break; //0x01
            case 2:
                byte16 = Byte.parseByte("00000010", 2);
                break; //0x02
            case 3:
                byte16 = Byte.parseByte("00000100", 2);
                break; //0x04
            case 4:
                byte16 = Byte.parseByte("00001000", 2);
                break; //0x08
            case 5:
                byte16 = Byte.parseByte("00010000", 2);
                break; //0x10
            case 6:
                byte16 = Byte.parseByte("00100000", 2);
                break; //0x20
            case 7:
                byte16 = Byte.parseByte("01000000", 2);
                break; //0x40
        }

        navigationCommandCharacteristic_value[16] = byte16;
        navigationCommandCharacteristic.setValue(navigationCommandCharacteristic_value);

    }
}
