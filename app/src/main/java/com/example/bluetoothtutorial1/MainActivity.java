package com.example.bluetoothtutorial1;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

// ================================================================================================
// BluetoothTutorial1:
//      - Use a button to turn on and off bluetooth
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;

    // Create broadcast receiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // if bluetooth is turned on/off
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                // The state is received as an extra from the intent
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE_ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE_TURNING_ON");
                        break;
                }

            }
        }
    };

    // Second broadcast receiver for toggle discoverability
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // if discoverability mode changed
            if (action.equals(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                // The state is received as an extra from the intent
                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, mBluetoothAdapter.ERROR);

                switch(mode) {
                    // Discoverability enabled and able to receive connections
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "onReceive: SCAN_MODE_CONNECTABLE_DISCOVERABLE. Discoverability enabled");
                        break;

                    // Discoverability not enabled, but able to receive connections
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver1: SCAN_MODE_CONNECTABLE. Discoverability disabled");
                        break;

                    // Discoverability disabled
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver1: SCAN_MODE_NONE. Discoverability disabled");
                        break;

                    // Connecting
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver1: STATE_CONNECTING");
                        break;

                    // Connected
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver1: STATE_CONNECTED");
                        break;
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define buttons from xml
        Button btn_on_off = findViewById(R.id.btn_on_off);
        Button btn_toggle_discoverability = findViewById(R.id.btn_toggle_discoverability);

        // Define the bluetooth adapter on phone
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Call enable/disable when button pressed
        btn_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Button on/off pressed.");
                enableDisableBT();
            }
        });

        btn_toggle_discoverability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Button discoverability presed");
                enableDisableDiscoverability();
            }
        });
    }

    // Turn off the broadcast receiver when destroyed
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Method called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
    }

    // Enable/Disable bluetooth method
    public void enableDisableBT() {
        // Case 1: Phone doesn't have bluetooth
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }

        // Case 2: Bluetooth is off
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDIsableBT: Enabling BT.");

            // Create dialog box to enable bluetooth
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

        // Case 3: Bluetooth is on
        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: Disabling BT.");

            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }

    public void enableDisableDiscoverability() {
        Log.d(TAG, "enableDisableDiscoverability: Making device discoverable for 300 secs.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);
    }
}
