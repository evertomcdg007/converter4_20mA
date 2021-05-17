package com.saga.converter_4_20ma.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.saga.converter_4_20ma.R
import com.saga.converter_4_20ma.interfaces.Interfaces
import java.util.*


class ListDeviceBluetoothAdapter(
    context: Context,
    textViewResourceId: Int,
    list: ArrayList<BluetoothDevice>
) : ArrayAdapter<BluetoothDevice?>(context, textViewResourceId, list as List<BluetoothDevice?>) {

    private val listDevices: ArrayList<BluetoothDevice>
    private var holder: ViewHolder? = null

    //
    private var onClickAdapter: Interfaces.OnClickAdapter? = null

    /**
     * @param
     * @return
     */
    private inner class ViewHolder {
        var textLabel: TextView? = null
        var textMac: TextView? = null
        var relatLine: RelativeLayout? = null
    }

    /**
     * @param
     * @return
     */
    @SuppressLint("InflateParams")
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        if (DEBUGGER) Log.d(
            TAG,
            "getView"
        )
        if (convertView == null) {
            val vi =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = vi.inflate(R.layout.item_list_device_bt, null)
            holder = ViewHolder()
            holder!!.textLabel = convertView!!.findViewById<View>(R.id.textView1) as TextView?
            holder!!.textMac = convertView.findViewById<View>(R.id.textView2) as TextView?
            holder!!.relatLine = convertView.findViewById<View>(R.id.line1) as RelativeLayout?
            holder!!.relatLine!!.setOnClickListener { view ->
                val device = (view as RelativeLayout).tag as BluetoothDevice
                onClickAdapter?.onClickAdapterLintener(device)
            }
            convertView.setTag(holder)
        } else {
            holder = convertView.tag as ViewHolder
        }

        // ---------------------------
        // Get the data item for this position
        val item = getItem(position)

        // Status
        holder!!.textLabel!!.text = item!!.name
        // Duration
        holder!!.textMac!!.text = item.address
        holder!!.relatLine!!.tag = item
        return convertView
    }

    /**
     * @param
     * @return
     */
    fun setOnClickAdapter(listener: Interfaces.OnClickAdapter?) {
        onClickAdapter = listener
    }

    companion object {
        // DEBUGGER
        private const val DEBUGGER = false
        private val TAG = ListDeviceBluetoothAdapter::class.java.simpleName
    }

    /**
     * @param
     * @return
     */
    init {
        if (DEBUGGER) Log.d(
            TAG,
            "ListDeviceBluetoothAdapter"
        )
        //this.context = context
        listDevices = ArrayList<BluetoothDevice>()
        listDevices.addAll(list)
    }
}


