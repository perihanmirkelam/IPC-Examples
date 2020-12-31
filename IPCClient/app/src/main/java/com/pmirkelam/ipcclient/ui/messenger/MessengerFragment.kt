package com.pmirkelam.ipcclient.ui.messenger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pmirkelam.ipcclient.*
import com.pmirkelam.ipcclient.databinding.FragmentMessengerBinding


class MessengerFragment : Fragment(), ServiceConnection, View.OnClickListener {

    private var _binding: FragmentMessengerBinding? = null
    private val viewBinding get() = _binding!!

    // Is bound to the service of remote process
    private var isBound: Boolean = false

    // Messenger on the server
    private var serverMessenger: Messenger? = null

    // Messenger on the client
    private var clientMessenger: Messenger? = null

    // Handle messages from the remote service
    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
		    // Update UI with remote process info
            val bundle = msg.data
            viewBinding.linearLayoutClientInfo.visibility = View.VISIBLE
            viewBinding.btnConnect.text = getString(R.string.disconnect)
            viewBinding.txtServerPid.text = bundle.getInt(PID).toString()
            viewBinding.txtServerConnectionCount.text = bundle.getInt(CONNECTION_COUNT).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessengerBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewBinding.btnConnect.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(isBound){
            doUnbindService()
        } else {
            doBindService()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        serverMessenger = Messenger(service)
        // Ready to send messages to remote service
        sendMessageToServer()
    }

    override fun onServiceDisconnected(className: ComponentName) {
        clearUI()
        serverMessenger = null
    }

    private fun clearUI(){
        viewBinding.txtServerPid.text = ""
        viewBinding.txtServerConnectionCount.text = ""
        viewBinding.btnConnect.text = getString(R.string.connect)
        viewBinding.linearLayoutClientInfo.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        doUnbindService()
        super.onDestroy()
    }

    private fun doBindService() {
        clientMessenger = Messenger(handler)
        Intent("messengerexample").also { intent ->
            intent.`package` = "com.pmirkelam.ipcserver"
            activity?.applicationContext?.bindService(intent, this, Context.BIND_AUTO_CREATE)
        }
        isBound = true
    }

    private fun doUnbindService() {
        if (isBound) {
            activity?.applicationContext?.unbindService(this)
            isBound = false
        }
    }

    private fun sendMessageToServer() {
        if (!isBound) return
        val message = Message.obtain(handler)
        val bundle = Bundle()
        bundle.putString(DATA, viewBinding.edtClientData.text.toString())
        bundle.putString(PACKAGE_NAME, context?.packageName)
        bundle.putInt(PID, Process.myPid())
        message.data = bundle
        message.replyTo = clientMessenger // we offer our Messenger object for communication to be two-way
        try {
            serverMessenger?.send(message)
        } catch (e: RemoteException) {
            e.printStackTrace()
        } finally {
            message.recycle()
        }
    }
}
