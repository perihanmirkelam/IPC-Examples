package com.pmirkelam.ipcserver

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pmirkelam.ipcserver.databinding.ActivityLauncherBinding
import android.os.Process
import android.widget.Toast

class MainActivity : AppCompatActivity(), ServiceConnection, View.OnClickListener {

    private val TAG: String? = MainActivity::class.simpleName
    private lateinit var binding: ActivityLauncherBinding
    var iRemoteService: IIPCExample? = null
    private var connected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnConnect.setOnClickListener(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        // Gets an instance of the AIDL interface named IIPCExample,
        // which we can use to call on the service
        iRemoteService = IIPCExample.Stub.asInterface(service)
        binding.txtServerPid.text = iRemoteService?.pid.toString()
        binding.txtServerConnectionCount.text = iRemoteService?.connectionCount.toString()
        iRemoteService?.setDisplayedValue(
            applicationContext.packageName,
            Process.myPid(),
            binding.edtClientData.text.toString())
        connected = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Toast.makeText(this, "IPC server has disconnected unexpectedly", Toast.LENGTH_LONG).show()
        iRemoteService = null
        connected = false
    }

    override fun onClick(v: View?) {
        connected = if (connected) {
            disconnectToRemoteService()
            binding.txtServerPid.text = ""
            binding.txtServerConnectionCount.text = ""
            binding.btnConnect.text = getString(R.string.connect)
            binding.linearLayoutClientInfo.visibility = View.INVISIBLE
            false
        } else {
            connectToRemoteService()
            binding.linearLayoutClientInfo.visibility = View.VISIBLE
            binding.btnConnect.text = getString(R.string.disconnect)
            true
        }
    }

    override fun onDestroy() {
        disconnectToRemoteService()
        super.onDestroy()
    }

    private fun connectToRemoteService() {
        val intent = Intent("aidlexample")
        val pack = IIPCExample::class.java.`package`
        pack?.let {
            intent.setPackage(pack.name)
            applicationContext.bindService(
                intent, this, Context.BIND_AUTO_CREATE
            )
        }
    }

    private fun disconnectToRemoteService() {
        if(connected){
            applicationContext.unbindService(this)
        }
    }

}