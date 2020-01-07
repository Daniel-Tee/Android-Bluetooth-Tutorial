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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define button from xml
        Button btn_on_off = findViewById(R.id.btn_on_off);

        // Define the bluetooth adapter on phone
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Call enable/disable when button pressed
        btn_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Button pressed.");
                enableDisableBT();
            }
        });
    }

    // Turn off the broadcast receiver when destroyed
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: Method called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
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
}
