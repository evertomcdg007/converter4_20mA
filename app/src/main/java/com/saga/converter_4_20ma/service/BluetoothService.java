package com.saga.converter_4_20ma.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.saga.converter_4_20ma.enumeration.TimeOutEnum;
import com.saga.converter_4_20ma.service.cmd.CommandPackBt;
import com.saga.converter_4_20ma.utilities.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class BluetoothService {

    // DEBUGGER
    private static final boolean DEBUGGER = true;
    private static final String TAG = BluetoothService.class.getSimpleName();

    public static final String BLUETOOTH_SERVER_STATUS = "BLUETOOTH_SERVER_STATUS";
    public static final String BLUETOOTH_SERVER_CONNECTION_DOWN = "BLUETOOTH_SERVER_CONNECTION_DOWN";

    // Singleton class
    private static BluetoothService instance = null;
    private static Object mutex = new Object();

    private Context context;

    // android built in classes for bluetooth operations
    private BluetoothAdapter bluetoothAdapter;
    private static BluetoothSocket socket;
    private static BluetoothDevice device = null;

    private static OutputStream outputStream;
    private static InputStream inputStream;

    private byte[] readBuffer;

    private static BluetoothDevice bluetoothDevice = null;

    //
    private boolean waitingTimeOut = false;

    /**
     * @param
     * @return
     */
    public static BluetoothService getInstance() {

        if (instance == null) {
            synchronized (mutex) {
                if (instance == null) {
                    instance = new BluetoothService();
                }
            }
        }
        return instance;
    }

    /**
     * @param
     * @return
     * @throws IOException
     */
    public void startServerBluetooth(Context context, BluetoothDevice device) throws IOException {

        if (DEBUGGER)
            Log.d(TAG, "startServerBluetooth");

        //
        this.context = context;
        bluetoothDevice = device;

        // -----------------------------------------
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothService.device = bluetoothAdapter.getRemoteDevice(bluetoothDevice.getAddress());

        // -----------------------------------------
        // Standard SerialPortService ID
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        socket = BluetoothService.device.createRfcommSocketToServiceRecord(uuid);
        socket.connect();
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
    }

    /**
     * @param
     * @return
     */
    public void stopServerBluetooth(boolean flag) {

        try {
            closeComm();
            bluetoothDevice = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public synchronized byte[] receiveData(TimeOutEnum timeout) throws IOException, TimeoutException {

        //
        Timer timer = new Timer(timeout.getValue());
        timer.start();

        byte[] buffer = new byte[1000];
        String str = "";
        int value = 0;
        int idx = 0;

        while (true) {
            if (socket.isConnected()) {
                if (inputStream.available() > 0) {

                    waitTimeOutReceive();

                    inputStream.read(buffer, 0, 1000);
                    // Ajusta o tamanho do buffer de trabalho para o tamanho real do pacote.
                    int delimiter = (CommandPackBt.SIZE_BT_HEADER_PACK + (buffer[CommandPackBt.idx_BT_LENGTH]) & 0xFF);
                    byte[] readBuffer = new byte[delimiter];

                    // transfere o pacote do buffer do BT para o buffer de trabalho.
                    for (int i = 0; i < delimiter; i++) {
                        readBuffer[i] = buffer[i];
                    }
                    return readBuffer;
                }
                if (timer.isExpired()) {
                    throw new TimeoutException();
                }
            } else {
                throw new IOException();
            }
        }
    }

    /**
     * @param
     * @return
     */
    public boolean sendData(byte[] arg_msg) throws IOException {
        outputStream.write(arg_msg);
        return true;
    }

    public String readData() throws IOException{
        String data = "";
        byte[] readData = new byte[7];
        int r = inputStream.read(readData, 0 ,readData.length);
        if(r != -1) {
            data = Utils.arrayBytesToStringHex(readData);
        }
        return data;

    }



    /**
     * Close the connection to bluetooth
     *
     * @param
     * @return
     */
    private void closeComm() throws IOException {

        try {
//            stopWorker = true;
            outputStream.close();
            inputStream.close();
            socket.close();
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return
     */
    private void waitTimeOutReceive() {

        //
        Timer timer = new Timer(TimeOutEnum.RECEIVE_HEADER.getValue());
        timer.start();
        while (true) {
            if (timer.isExpired()) {
                break;
            }
        }
    }

    /**
     * @param
     * @return
     */
    public boolean isBluetoothConnected() {
        if (socket != null) {
            return socket.isConnected();
        }
        return false;
    }

    /**
     * @param
     * @return
     */
    public BluetoothDevice getConnectedBluetoothDevice() {
        return bluetoothDevice;
    }
}
