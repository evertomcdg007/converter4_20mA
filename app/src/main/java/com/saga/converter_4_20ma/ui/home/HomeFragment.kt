package com.saga.converter_4_20ma.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.saga.converter_4_20ma.R
import com.saga.converter_4_20ma.enumeration.LengthPacked
import com.saga.converter_4_20ma.enumeration.OperationTypeEnum
import com.saga.converter_4_20ma.service.BluetoothService
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    //BEGIN##########################################
    //##https://github.com/appsinthesky/Kotlin-Serial-Usb/blob/master/app/src/main/java/com/appsinthesky/kotlinusb/MainActivity.kt
    //#########################################
//    lateinit var m_usbManager: UsbManager
//    var m_device: UsbDevice? = null
//    var m_serial: UsbSerialDevice? = null
//    var m_connection: UsbDeviceConnection? = null
//    val ACTION_USB_PERMISSION = "permission"
    //END##########################################
    //##https://github.com/appsinthesky/Kotlin-Serial-Usb/blob/master/app/src/main/java/com/appsinthesky/kotlinusb/MainActivity.kt
    //#########################################

    var bluetoothService: BluetoothService? = null
    var q3Spinner: Spinner? = null
    var rangeSpinner: Spinner? = null
    var timeSpinner: Spinner? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        q3Spinner = root.findViewById(R.id.q3_spinner)
        rangeSpinner = root.findViewById(R.id.range_spinner)
        timeSpinner = root.findViewById(R.id.time_spinner)

        bluetoothService = BluetoothService.getInstance()


//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })



        //BEGIN##########################################
        //##https://github.com/appsinthesky/Kotlin-Serial-Usb/blob/master/app/src/main/java/com/appsinthesky/kotlinusb/MainActivity.kt
        //#########################################
//        m_usbManager = activity?.getSystemService(Context.USB_SERVICE) as UsbManager
//
//        val filter = IntentFilter()
//        filter.addAction(ACTION_USB_PERMISSION)
//        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
//        activity?.registerReceiver(broadcastReceiver, filter)

//        on.setOnClickListener{ sendData("o") }
//        off.setOnClickListener{ sendData("x") }
//        disconnect.setOnClickListener { disconnect() }
//        connect.setOnClickListener { startUsbConnecting() }
        //END##########################################
        //##https://github.com/appsinthesky/Kotlin-Serial-Usb/blob/master/app/src/main/java/com/appsinthesky/kotlinusb/MainActivity.kt
        //#########################################

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var q3 = 0.0
        var range = 0.0
        var time = 0

        val q3FlowRates = resources.getStringArray(R.array.q3)
        if (q3Spinner != null) {
            q3Spinner!!.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, q3FlowRates)
            q3Spinner!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    if(selectedItem != q3FlowRates[0].toString()){
                        Toast.makeText(
                            context,
                            getString(R.string.selected_q3)+" "+q3FlowRates[position]+"L/h", Toast.LENGTH_SHORT
                        ).show()
                        q3 = q3FlowRates[position].toDouble()
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        val ranges = resources.getStringArray(R.array.range)
        if (rangeSpinner != null) {
            rangeSpinner!!.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ranges)
            rangeSpinner!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    if(selectedItem != ranges[0].toString()){
                        Toast.makeText(
                            context,
                            getString(R.string.selected_range)+" "+ranges[position], Toast.LENGTH_SHORT
                        ).show()
                        range = ranges[position].toDouble()
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        val timesAcq = resources.getStringArray(R.array.time)
        if (timeSpinner != null) {
            timeSpinner!!.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timesAcq)

            timeSpinner!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    if(selectedItem != timesAcq[0]){
                        Toast.makeText(
                            context,
                            getString(R.string.selected_time) + " " +
                                    "" + timesAcq[position]+" segundos", Toast.LENGTH_SHORT
                        ).show()
                        time = parent.getItemAtPosition(position).toString().toInt()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
            set_config_button.setOnClickListener { sendCommand(q3, range, time) }
            get_config_button.setOnClickListener { getConfig() }

        }

    }

    private fun getConfig() {

        if(bluetoothService!!.isBluetoothConnected){

            var cmdRange = ByteArray(3)
            cmdRange[0] = OperationTypeEnum.CMD_PRE_VALID.ordinal.toByte()
            cmdRange[1] = OperationTypeEnum.CMD_GET_RANGE.ordinal.toByte()
            cmdRange[2] = LengthPacked.LENGTH_PACK_0.ordinal.toByte()

            bluetoothService!!.sendData(cmdRange)

            var range = bluetoothService!!.readData()

            var cmdQ3 = ByteArray(3)
            cmdQ3[0] = OperationTypeEnum.CMD_PRE_VALID.ordinal.toByte()
            cmdQ3[1] = OperationTypeEnum.CMD_GET_Q3.ordinal.toByte()
            cmdQ3[2] = LengthPacked.LENGTH_PACK_0.ordinal.toByte()

            bluetoothService!!.sendData(cmdQ3)

            var q3 = bluetoothService!!.readData()

            var cmdAcqTime = ByteArray(3)
            cmdAcqTime[0] =  0xFF.toByte()//OperationTypeEnum.CMD_PRE_VALID.ordinal.toByte()
            cmdAcqTime[1] =  0x02//OperationTypeEnum.CMD_GET_ACQUIRE_TIME.ordinal.toByte()
            cmdAcqTime[2] =  0x00//LengthPacked.LENGTH_PACK_0.ordinal.toByte()

            bluetoothService!!.sendData(cmdQ3)

            var acqTime = bluetoothService!!.readData()

            Log.i("TAG INFO", "Range: "+range+" Q3: "+q3+" acqTime: "+acqTime)


        }else{
            Toast.makeText(context, getString(R.string.BTnConectado), Toast.LENGTH_SHORT).show()
        }


    }


    fun sendCommand(q3: Double, range: Double, timeAcq: Int){


        if(bluetoothService!!.isBluetoothConnected){



        }else{
            Toast.makeText(context, getString(R.string.BTnConectado), Toast.LENGTH_SHORT).show()
        }

    }

    //BEGIN##########################################
    //##https://github.com/appsinthesky/Kotlin-Serial-Usb/blob/master/app/src/main/java/com/appsinthesky/kotlinusb/MainActivity.kt
    //#########################################
//    private fun startUsbConnecting() {
//        val usbDevices: HashMap<String, UsbDevice>? = m_usbManager.deviceList
//        if (!usbDevices?.isEmpty()!!) {
//            var keep = true
//            usbDevices.forEach{ entry ->
//                m_device = entry.value
//                val deviceVendorId: Int? = m_device?.vendorId
//                Log.i("serial", "vendorId: "+deviceVendorId)
//                if (deviceVendorId == 1027) {
//                    val intent: PendingIntent = PendingIntent.getBroadcast( activity, 0, Intent(ACTION_USB_PERMISSION), 0)
//                    m_usbManager.requestPermission(m_device, intent)
//                    keep = false
//                    Log.i("serial", "connection successful")
//                } else {
//                    m_connection = null
//                    m_device = null
//                    Log.i("serial", "unable to connect")
//                }
//                if (!keep) {
//                    return
//                }
//            }
//        } else {
//            Log.i("serial", "no usb device connected")
//        }
//    }
//
//    private fun sendData(input: String) {
//        m_serial?.write(input.toByteArray())
//        Log.i("serial", "sending data: "+input.toByteArray())
//    }
//
//    private fun disconnect() {
//        m_serial?.close()
//    }
//
//    private val broadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if (intent?.action!! == ACTION_USB_PERMISSION) {
//                val granted: Boolean = intent.extras!!.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)
//                if (granted) {
//                    m_connection = m_usbManager.openDevice(m_device)
//                    m_serial = UsbSerialDevice.createUsbSerialDevice(m_device, m_connection)
//                    if (m_serial != null) {
//                        if (m_serial!!.open()) {
//                            m_serial!!.setBaudRate(115200)
//                            m_serial!!.setDataBits(UsbSerialInterface.DATA_BITS_8)
//                            m_serial!!.setStopBits(UsbSerialInterface.STOP_BITS_1)
//                            m_serial!!.setParity(UsbSerialInterface.PARITY_NONE)
//                            m_serial!!.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
//                        } else {
//                            Log.i("Serial", "Porta não está aberta.")
//                        }
//                    } else {
//                        Log.i("Serial", "Porta está nula.")
//                    }
//                } else {
//                    Log.i("Serial","Não tem permissão.")
//                }
//            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
//                startUsbConnecting()
//            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
//                disconnect()
//            }
//        }
//    }
    //END##########################################
    //##https://github.com/appsinthesky/Kotlin-Serial-Usb/blob/master/app/src/main/java/com/appsinthesky/kotlinusb/MainActivity.kt
    //#########################################


}