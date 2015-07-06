package se.cfor.solarmonitor;

import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {
    private int REQUEST_ENABLE_BT  = 1;

    private TextView mVoltageTextView;
    private TextView mCurrentTextView;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice btDevice;
    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothSocket btSocket;


    @Override  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button connectButton = (Button) findViewById(R.id.connectButton);

        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectBt();
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {
            connectBt();
        }
    }
    /*
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                //mCurrentTextView.setText(device.getName() + "\n" + device.getAddress());

            }
        }
    };

    protected void onActivityResult(int requestCode, int resultCode,
        Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                mVoltageTextView.setText("BT-på");
                //startActivity(new Intent(Intent.ACTION_VIEW, data));
            }
        }
    }
    */

    protected void connectBt() {
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Bluetooth Not Enabled! Enable and Retry!", Toast.LENGTH_SHORT).show();
        } else {


            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                boolean found = false;
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    if (device.getName().equals("HC-06")) {
                        device.fetchUuidsWithSdp();
                        MY_UUID = device.getUuids()[0].getUuid();

                        //Toast.makeText(getApplicationContext(), "UUID: " + device.getUuids()[0].getUuid(), Toast.LENGTH_SHORT).show();
                        new ConnectThread(mBluetoothAdapter.getRemoteDevice(device.getAddress())).start();

                        return;
                    }
                }
                Toast.makeText(getApplicationContext(), "Not Paired with HC-06, Pair and Retry!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Not Paired! Pair and Retry!", Toast.LENGTH_SHORT).show();
            }
            /*
            // Register the BroadcastReceiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
            */
        }


    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            btSocket = tmp;

        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                btSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    //Toast.makeText(getApplicationContext(), connectException.getMessage(), Toast.LENGTH_SHORT).show();
                    btSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            ((MyApplication)getApplication()).startBluetooth(btSocket);

            Intent intent = new Intent(getApplicationContext(),MainFragmentLauncher.class);
            startActivity(intent);


            // Do work to manage the connection (in a separate thread)
            //manageConnectedSocket(btSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException e) { }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = new Intent(getApplicationContext(),MainFragmentLauncher.class);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        try {
            btSocket.close();
        } catch (IOException e) { }

        super.onDestroy();
    }
}
