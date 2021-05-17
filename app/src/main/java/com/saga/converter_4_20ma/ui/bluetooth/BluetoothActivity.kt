package com.saga.converter_4_20ma.ui.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.saga.converter_4_20ma.R
import com.saga.converter_4_20ma.adapter.ListDeviceBluetoothAdapter
import com.saga.converter_4_20ma.dialogs.UserDialog
import com.saga.converter_4_20ma.interfaces.Interfaces
import com.saga.converter_4_20ma.service.BluetoothService
import com.saga.converter_4_20ma.service.TaskProgressService
import java.io.IOException
import java.util.*

class BluetoothActivity : AppCompatActivity() {

    // DEBUGGER
    private val DEBUGGER = true
    private val TAG = "SettingsBluetooth"

    val REQUEST_ENABLE_BT = 2

    private var context: Context? = null
    //
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var listDeviceBluetoothAdapter: ListDeviceBluetoothAdapter? = null
    private var listDevices = ArrayList<BluetoothDevice>()

    //
    private var tvConnectedLabel: TextView? = null
    private var tvConnectedMac: TextView? = null
    private var progress: TaskProgressService? = null
    private var pairedListView: ListView? = null

    //
    private var threadStartServerBluetooth: Thread? = null
    private var threadUpdateViewConnectedDevice: Thread? = null
    private var loopThreadUpdateView = true

    var textViewTitlePairedDevice: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setTitle("Conexão Bluetooth")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        context = this

        textViewTitlePairedDevice = findViewById(R.id.title_paired_devices)
        tvConnectedLabel = findViewById(R.id.textViewConnectedLabel)
        tvConnectedMac = findViewById(R.id.textViewConnectedMac)
        pairedListView = findViewById(R.id.paired_devices)
        listDeviceBluetoothAdapter = ListDeviceBluetoothAdapter(this, R.layout.activity_bluetooth, listDevices)
        pairedListView!!.adapter = listDeviceBluetoothAdapter

        //command for connect device
        listDeviceBluetoothAdapter!!.setOnClickAdapter(listener)
        //command for disconnect device

        startThreadUpdateConnectedDevice()
        loadProgressDialog()
        startConectBluetooth()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onClickConnectedDevice(v: View) {
        when (v.id) {
            R.id.line_device_connected ->
            if (BluetoothService.getInstance().isBluetoothConnected()) {
                val userDialog = UserDialog(context)
                userDialog.setDialogType(UserDialog.DIALOG_TYPE_ALERT)
                userDialog.setTextDialog("Deseja mesmo desconectar o dispositivo?")
                userDialog.setLabelPositiveButton(
                    context!!.resources.getString(R.string.DialogOk)
                )
                userDialog.setOnPositiveButtonClickListener(object : Interfaces.OnPositiveButtonDialog {
                    override fun onPositiveListener(arg: Any?) {
                        //
                        BluetoothService.getInstance().stopServerBluetooth(false)
                    }
                })
                userDialog.setLabelNegativeButton(
                    context!!.resources.getString(R.string.DialogCancel)
                )
                userDialog.show()
            }
        }
    }

    /**
     * @param
     * @return
     */
    private fun startConectBluetooth() {

        // Get local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            //
            //finish()
        } else if (!bluetoothAdapter!!.isEnabled()) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        } else {
            updateListPairedDevice()
        }
    }

    /**
     * @param
     * @return
     */
    private fun updateListPairedDevice() {

        // Get the local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        // Get a set of currently paired devices
        val pairedDevices: Set<BluetoothDevice> =
            bluetoothAdapter!!.getBondedDevices()
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size > 0) {
            textViewTitlePairedDevice?.visibility = View.VISIBLE
            listDeviceBluetoothAdapter!!.clear()
            for (device in pairedDevices) {
                listDeviceBluetoothAdapter!!.add(device)
                listDeviceBluetoothAdapter!!.notifyDataSetChanged()
            }
        }
    }

    /**
     * @param
     * @return
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG,"onActivityResult")

        when (requestCode) {
            REQUEST_ENABLE_BT ->
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    //
                    updateListPairedDevice()
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(context, "BLuetooth não habilitado", Toast.LENGTH_SHORT).show()
                    //
                    //finish()
                }
        }
    }


    /**
     * @param
     * @return
     */
    private val listener: Interfaces.OnClickAdapter = object : Interfaces.OnClickAdapter {
        override fun onClickAdapterLintener(arg0: Any?) {
            if (!BluetoothService.getInstance().isBluetoothConnected) {
                // Cancel discovery because it's costly and we're about to connect
                bluetoothAdapter?.cancelDiscovery()
                startThreadServerBluetooth(arg0 as BluetoothDevice)
            } else {
                val userDialog = UserDialog(context)
                userDialog.setDialogType(UserDialog.DIALOG_TYPE_POSITIVE)
                userDialog.setTextDialog(resources.getString(R.string.BTJaConectado))
                userDialog.setLabelPositiveButton(
                    context!!.resources.getString(R.string.DialogOk)
                )
                userDialog.show()
            }
        }
    }

    /**
     * @param
     * @return
     */
    private fun startThreadUpdateConnectedDevice() {
        val handler = Handler()
        threadUpdateViewConnectedDevice = Thread(Runnable {
            if (DEBUGGER)
                Log.d(TAG,"ThreadUpdateViewConnectedDevice")
            while (loopThreadUpdateView) {
                try {
                    Thread.sleep(2000)
                    //
                    handler.post {
                        val text = ""
                        if (BluetoothService.getInstance().isBluetoothConnected) {
                            val device =
                                BluetoothService.getInstance().connectedBluetoothDevice
                            tvConnectedLabel!!.text = device.name
                            tvConnectedMac!!.text = device.address
                        } else {
                            tvConnectedLabel!!.text = "-"
                            tvConnectedMac!!.text = "-"
                        }
                        if (DEBUGGER)
                            Log.d(TAG, "UpdateView: $text")
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        })
        threadUpdateViewConnectedDevice!!.name = "ThreadUpdateViewConnectedDevice"
        threadUpdateViewConnectedDevice!!.start()
    }

    /**
     * @param
     * @return
     */
    private fun loadProgressDialog() {
        progress = TaskProgressService(context!!)
        progress!!.setUserMessage("Conectando ...")
    }

    /**
     * @param
     * @return
     */
    private fun startProgressDialog() {
        progress!!.start()
        progress!!.show()
    }

    /**
     * @param
     * @return
     */
    private fun stopProgressDialog() {
        // Kill the popup
        progress!!.finish()
    }

    /**
     * @param
     * @return
     */
    private val listenerPositiveButton: Interfaces.OnPositiveButtonDialog = object : Interfaces.OnPositiveButtonDialog {
        override fun onPositiveListener(arg: Any?) {
            if (BluetoothService.getInstance().isBluetoothConnected) {
//                BluetoothService.getInstance().startServerBluetooth();
                Toast.makeText(context,
                    "Conexão com a placa estabelecida",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * @param
     * @return
     */
    private fun startThreadServerBluetooth(info: BluetoothDevice) {
        startProgressDialog()
        val handler = Handler()
        threadStartServerBluetooth = Thread(Runnable {
            try {
                BluetoothService.getInstance().startServerBluetooth(context, info)
                //                    BluetoothService.getInstance().initServerBluetooth(context, info);
                //                    BluetoothService.getInstance().startServerBluetooth();
                stopProgressDialog()
                //
                handler.post {
                    val userDialog = UserDialog(context)
                    userDialog.setDialogType(UserDialog.DIALOG_TYPE_POSITIVE)
                    userDialog.setTextDialog(resources.getString(R.string.BTConectado))
                    userDialog.setLabelPositiveButton(
                        context!!.resources.getString(R.string.DialogOk)
                    )
                    userDialog.setOnPositiveButtonClickListener(listenerPositiveButton)
                    userDialog.show()
                }
            } catch (e: IOException) {
                stopProgressDialog()
                handler.post {
                    val userDialog = UserDialog(context)
                    userDialog.setDialogType(UserDialog.DIALOG_TYPE_NEGATIVE)
                    userDialog.setTextDialog(resources.getString(R.string.BTnConectado))
                    userDialog.setLabelPositiveButton(
                        context!!.resources.getString(R.string.DialogOk)
                    )
                    userDialog.setOnPositiveButtonClickListener(listenerPositiveButton)
                    userDialog.show()
                }
            }
        })
        threadStartServerBluetooth!!.name = "ThreadStartServerBluetooth"
        threadStartServerBluetooth!!.start()
    }

    /**
     * @param
     * @return
     */
    override fun onResume() {
        super.onResume()
    }

    /**
     * @param
     * @return
     */
    override fun onDestroy() {
        super.onDestroy()
        if (DEBUGGER)
            Log.d(TAG,"onDestroy")

        // Clean thread start server bluetooth
        if (threadStartServerBluetooth != null) {
            threadStartServerBluetooth!!.interrupt()
            threadStartServerBluetooth = null
        }
        // Clean thread update view connected device
        if (threadUpdateViewConnectedDevice != null) {
            loopThreadUpdateView = false
            threadUpdateViewConnectedDevice!!.interrupt()
            threadUpdateViewConnectedDevice = null
        }
    }

}