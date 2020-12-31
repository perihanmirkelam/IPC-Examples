package com.pmirkelam.ipcserver

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pmirkelam.ipcserver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val client = RecentClient.client
        binding.connectionStatus.text =
            if (client == null) {
                binding.linearLayoutClientState.visibility = View.INVISIBLE
                getString(R.string.no_connected_client)
            } else {
                binding.linearLayoutClientState.visibility = View.VISIBLE
                getString(R.string.last_connected_client_info)
            }
        binding.txtPackageName.text = client?.clientPackageName
        binding.txtServerPid.text = client?.clientProcessId
        binding.txtData.text = client?.clientData
        binding.txtIpcMethod.text = client?.ipcMethod
    }
}
