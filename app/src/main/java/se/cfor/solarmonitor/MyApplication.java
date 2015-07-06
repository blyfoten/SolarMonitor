package se.cfor.solarmonitor;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by FORSLUNC on 2015-03-22.
 */
public class MyApplication extends Application {
    private ArrayList<DataListener>dataListeners = new ArrayList<DataListener>();
    private String newData;
    private boolean dataReceived = true;
    ConnectedThread connectedThread = null;

    public void addDataListener(DataListener dataListener) {
        System.out.println("Added listener: " + dataListener.toString());
        this.dataListeners.add(dataListener);
    }
    public void removeDataListener(DataListener dataListener) {
        System.out.println("Removed listener: " + dataListener.toString());
        this.dataListeners.remove(dataListener);
    }
    public void startBluetooth(BluetoothSocket btSocket)
    {
        connectedThread = new ConnectedThread(btSocket);
        connectedThread.start();
    }

    private synchronized void sendNewData() {
        for (DataListener btDataListener : this.dataListeners) {
            btDataListener.onNewData(newData);
        }
        newData = "";
    }

    private class ConnectedThread extends Thread {
        private BluetoothSocket btSocket = null;
        private Socket wifiSocket = null;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private boolean useWifi = true;

        public ConnectedThread(BluetoothSocket socket) {
            btSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.useWifi = false;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public ConnectedThread(Socket socket) {
            this.wifiSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.useWifi = true;
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {
            (new DataListenerFeeder(this)).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(mmInStream));
            String line;


            // Keep listening to the InputStream until an exception occurs
            try {
                while ((line = br.readLine()) != null) {
                    pushData(line);
                }

            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                if (this.useWifi)
                    wifiSocket.close();
                else
                    btSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private synchronized void pushData(String data) {
        newData = newData + data + "\n";
        this.dataReceived = true;
    }

    private class DataListenerFeeder extends Thread {
        private Thread hostThread;
        public DataListenerFeeder(Thread host) {
            this.hostThread = host;
        }

        @Override
        public void run() {
            try   {
                while (hostThread.isAlive())
                {
                    if (isDataReceived()) {
                        setDataReceived(false);
                        Thread.sleep(100);
                        if (isDataReceived() == false)
                        {
                            sendNewData();
                        }

                    }
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {

            }

        }
    }
    private synchronized void setDataReceived(boolean isReceived)
    {
        dataReceived = isReceived;
    }

    public void writeToSocket(String text) {
        if (connectedThread.isAlive())
            try {
                connectedThread.write(text.getBytes("US-ASCII"));
            }catch (UnsupportedEncodingException e) {
            }
    }

    private synchronized boolean isDataReceived(){
        return dataReceived;
    }
}


