package com.example.android_obd2_bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Bluetooths(
    val name: String,
    val adress: String
)

class MainActivity : AppCompatActivity() {

    val bluetoothDevices: MutableList<Bluetooths> = mutableListOf()

    var adapter = BluetoothDeviceAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_list)
        var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)

        val button_scanner = findViewById<Button>(R.id.scanner)

        button_scanner.setOnClickListener(){

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return@setOnClickListener
            }
            bluetoothAdapter.startDiscovery()
        }

        // Register Broadcast
        val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, intentFilter)

        // Atualizar o layout
        recyclerView.adapter = adapter
    }

    val receiver  = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if(BluetoothDevice.ACTION_FOUND == action) {
                var device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    bluetoothDevices.add(Bluetooths(device.name, device.address))
                    adapter.onUpdate(bluetoothDevices)
                }
            }

        }
    }
}

class BluetoothDeviceAdapter(val bluetooths : List <Bluetooths>): RecyclerView.Adapter<ViewHolder>() {

    var bluetoothList: List<Bluetooths> = bluetooths

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bluetooth_itens, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onUpdate(bluetoothList[position])
    }

    override fun getItemCount(): Int {
        return bluetooths.size
    }

    fun onUpdate(bluetooths : List <Bluetooths>){
        bluetoothList = bluetooths
        notifyDataSetChanged()
    }

}

class ViewHolder(root: View): RecyclerView.ViewHolder(root) {
    fun onUpdate(bluetooths: Bluetooths){
        var name = itemView.findViewById<TextView>(R.id.name_device)
        name.text = bluetooths.name

        var address = itemView.findViewById<TextView>(R.id.address_device)
        address.text = bluetooths.adress
    }
}