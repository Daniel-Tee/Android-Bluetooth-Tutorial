package com.example.bluetoothtutorial1;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

// ================================================================================================
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";

    Button btn_on_off;
    Button btn_toggle_discoverability;
    Button btn_discover;
    ListView list_new_devices;

    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;

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

    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.activity_device_list_adapter, mBTDevices);
                list_new_devices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Case 1: Already bonded
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "onReceive: BOND_BONDED.");
                }

                // Case 2: Creating a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "onReceive: BOND_BONDING.");
                }

                // Case 3: Breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "onReceive: BOND_NONE.");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define buttons from xml
        btn_on_off = findViewById(R.id.btn_on_off);
        btn_toggle_discoverability = findViewById(R.id.btn_toggle_discoverability);
        btn_discover = findViewById(R.id.btn_discover);

        // Define list view
        list_new_devices = findViewById(R.id.list_new_devices);
        mBTDevices = new ArrayList<>();

        // Define the bluetooth adapter on phone
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Pairing receiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        list_new_devices.setOnItemClickListener(MainActivity.this);

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

        btn_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Button discover pressed");
                discoverDevice();
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
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
    }

    // Enable/Disable bluetooth button method
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

    // Toggle discoverability button method
    public void enableDisableDiscoverability() {
        Log.d(TAG, "enableDisableDiscoverability: Making device discoverable for 300 secs.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);
    }

    // Discover device button method
    public void discoverDevice() {
        Log.d(TAG, "discoverDevice: Looking for unpaired devices...");

        // If already discovering
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "discoverDevice: Cancelling discovery.");

            // Check bluetooth permissions for compatibility with > Lollipop
            checkBTPermission();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }

        // If not discovering
        if (!mBluetoothAdapter.isDiscovering()) {
            // Check bluetooth permissions for compatibility with > Lollipop
            checkBTPermission();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }

    }

    @TargetApi(23)
    private void checkBTPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOACTION");

            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        } else {
            Log.d(TAG, "checkBTPermissions: Version not higher than lollipop, no need to check");
        }
    }

   public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
       // Cancel discovery as it's very memory intensive
       mBluetoothAdapter.cancelDiscovery();

       Log.d(TAG, "onItemClick: Device clicked.");
       String deviceName = mBTDevices.get(i).getName();
       String deviceAddress = mBTDevices.get(i).getAddress();

       Log.d(TAG, "onItemClick: deviceName: " + deviceName);
       Log.d(TAG, "onItemClick: deviceAddress: " + deviceAddress);

       // Create the bond
       // Requires API 18+
       if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
           Log.d(TAG, "onItemClick: Trying to pair with " + deviceName);
           mBTDevices.get(i).createBond();
       } else {
           Log.d(TAG, "onItemClick: API level too low. Cannot pair.");
       }
   }

}
