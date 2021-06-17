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
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class RemoteControlServiceFragment extends ServiceFragment {

    //RCS UUID
    private static final UUID REMOTE_CONTROL_SERVICE_UUID = UUID
            .fromString("71ced1ac-0100-44f5-9454-806ff70b3e02");

    //RCS Characteristic 3 UUID
    private static final UUID RCS_CHARACT_UUID = UUID
            .fromString("71ced1ac-0103-44f5-9454-806ff70b3e02");
    //RCS Description
    private static final String REMOTE_CONTROL_SERVICE_DESCRIPTION = "Remote Control";

    //Service Delegate
    private ServiceFragmentDelegate mDelegate;

    //UI
    View.OnTouchListener btnActionListener;
    Button upBtn;
    Button centerBtn;
    Button leftBtn;
    Button rightBtn;
    Button downBtn;
    Button exitBtn;

    //RCS Characteristic 3 Variables
    byte[] rcsChar3_value = new byte[32];
    byte rcsChar3_byte17;
    byte rcsChar3_byte18;

    private final OnClickListener mNotifyButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mDelegate.sendNotificationToDevices(remoteControlServiceCharacteristic);
        }
    };

    // GATT
    private BluetoothGattService remoteControlService;
    private BluetoothGattCharacteristic remoteControlServiceCharacteristic;

    public RemoteControlServiceFragment() {
        //Set the defautl value 18th Byte of the RCS Characteristic 3

        remoteControlServiceCharacteristic =
                new BluetoothGattCharacteristic(RCS_CHARACT_UUID,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        remoteControlServiceCharacteristic.addDescriptor(
                Peripheral.getClientCharacteristicConfigurationDescriptor());

        remoteControlServiceCharacteristic.addDescriptor(
                Peripheral.getCharacteristicUserDescriptionDescriptor(REMOTE_CONTROL_SERVICE_DESCRIPTION));

        remoteControlServiceCharacteristic.setValue(rcsChar3_value);

        remoteControlService = new BluetoothGattService(REMOTE_CONTROL_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        remoteControlService.addCharacteristic(remoteControlServiceCharacteristic);
    }

    // Lifecycle callbacks
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_remote_control_service, container, false);

        createBtnActionListener();

        upBtn = (Button) view.findViewById(R.id.upBtn);
        upBtn.setOnTouchListener(btnActionListener);
        centerBtn = (Button) view.findViewById(R.id.centerBtn);
        centerBtn.setOnTouchListener(btnActionListener);
        leftBtn = (Button) view.findViewById(R.id.leftBtn);
        leftBtn.setOnTouchListener(btnActionListener);
        rightBtn = (Button) view.findViewById(R.id.rightBtn);
        rightBtn.setOnTouchListener(btnActionListener);
        downBtn = (Button) view.findViewById(R.id.downBtn);
        downBtn.setOnTouchListener(btnActionListener);
        exitBtn = (Button) view.findViewById(R.id.exitBtn);
        exitBtn.setOnTouchListener(btnActionListener);

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
        return remoteControlService;
    }

    @Override
    public ParcelUuid getServiceUUID() {
        return new ParcelUuid(REMOTE_CONTROL_SERVICE_UUID);
    }


    @Override
    public void notificationsEnabled(BluetoothGattCharacteristic characteristic, boolean indicate) {
        if (characteristic.getUuid() != RCS_CHARACT_UUID) {
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
        if (characteristic.getUuid() != RCS_CHARACT_UUID) {
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

    public void createBtnActionListener() {
        btnActionListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String button = ((Button) v).getText().toString();


                //Check if the button is clicked or released
                String action = "";
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    action = "down";
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    action = "up";
                }

                //Set Byte 17 (RC Mode)
                rcsChar3_value[16] = rcsChar3_byte17;

                //Change the specific bit of byte 18
                switch (button) {
                    case "UP": //Up clicked or released
                        if (action.equals("down")) {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 | (1 << 3));
                        } else {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 & ~(1 << 3));
                        }
                        rcsChar3_value[17] = rcsChar3_byte18;
                        remoteControlServiceCharacteristic.setValue(rcsChar3_value);
                        Log.i("RCS", "Changed RCS Value: " + new String(remoteControlServiceCharacteristic.getValue(), StandardCharsets.UTF_8));
                        break;
                    case "CENTER": //Center clicked or released
                        if (action.equals("down")) {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 | (1 << 0));
                        } else {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 & ~(1 << 0));
                        }
                        rcsChar3_value[17] = rcsChar3_byte18;
                        remoteControlServiceCharacteristic.setValue(rcsChar3_value);
                        Log.i("RCS", "Changed RCS Value: " + new String(remoteControlServiceCharacteristic.getValue(), StandardCharsets.UTF_8));
                        break;
                    case "LEFT": //Left clicked or released
                        if (action.equals("down")) {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 | (1 << 4));
                        } else {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 & ~(1 << 4));
                        }
                        rcsChar3_value[17] = rcsChar3_byte18;
                        remoteControlServiceCharacteristic.setValue(rcsChar3_value);
                        Log.i("RCS", "Changed RCS Value: " + new String(remoteControlServiceCharacteristic.getValue(), StandardCharsets.UTF_8));
                        break;
                    case "RIGHT": //Right clicked or released
                        if (action.equals("down")) {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 | (1 << 5));
                        } else {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 & ~(1 << 5));
                        }
                        rcsChar3_value[17] = rcsChar3_byte18;
                        remoteControlServiceCharacteristic.setValue(rcsChar3_value);
                        Log.i("RCS", "Changed RCS Value: " + new String(remoteControlServiceCharacteristic.getValue(), StandardCharsets.UTF_8));
                        break;
                    case "DOWN":
                        if (action.equals("down")) {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 | (1 << 2));
                        } else {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 & ~(1 << 2));
                        }
                        rcsChar3_value[17] = rcsChar3_byte18;
                        remoteControlServiceCharacteristic.setValue(rcsChar3_value);
                        Log.i("RCS", "Changed RCS Value: " + new String(remoteControlServiceCharacteristic.getValue(), StandardCharsets.UTF_8));
                        break;
                    case "EXIT":
                        if (action.equals("down")) {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 | (1 << 1));
                        } else {
                            rcsChar3_byte18 = (byte) (rcsChar3_byte18 & ~(1 << 1));
                        }
                        rcsChar3_value[17] = rcsChar3_byte18;
                        remoteControlServiceCharacteristic.setValue(rcsChar3_value);
                        Log.i("RCS", "Changed RCS Value: " + new String(remoteControlServiceCharacteristic.getValue(), StandardCharsets.UTF_8));
                        break;
                }

                return true;
            }

        };
    }
}
