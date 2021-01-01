package com.pmirkelam.ipcclient.ui.broadcast

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pmirkelam.ipcclient.DATA
import com.pmirkelam.ipcclient.PACKAGE_NAME
import com.pmirkelam.ipcclient.PID
import com.pmirkelam.ipcclient.databinding.FragmentBroadcastBinding
import android.os.Process
import java.util.*
import kotlin.time.ExperimentalTime


class BroadcastFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentBroadcastBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBroadcastBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.btnConnect.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        sendBroadcast()
        showBroadcastTime()
    }

    private fun sendBroadcast(){
        val intent = Intent()
        intent.action = "com.pmirkelam.ipcclient"
        intent.putExtra(PACKAGE_NAME, context?.packageName)
        intent.putExtra(PID, Process.myPid().toString())
        intent.putExtra(DATA, binding.edtClientData.text.toString())
        intent.component = ComponentName("com.pmirkelam.ipcserver", "com.pmirkelam.ipcserver.IPCBroadcastReceiver")
        activity?.applicationContext?.sendBroadcast(intent)
    }

    private fun showBroadcastTime(){
        val cal = Calendar.getInstance()
        val time ="${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}"
        binding.linearLayoutClientInfo.visibility = View.VISIBLE
        binding.txtDate.text = time
    }
}
