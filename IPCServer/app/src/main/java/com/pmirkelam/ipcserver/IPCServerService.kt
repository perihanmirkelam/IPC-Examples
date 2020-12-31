package com.pmirkelam.ipcserver

import android.app.Service
import android.content.Intent
import android.os.*
import android.text.TextUtils


class IPCServerService : Service() {

    companion object {
        // How many connection requests have been received since the service started
        var connectionCount: Int = 0

        // Client might have sent an empty data
        const val NOT_SENT = "Not sent!"
    }

    // Messenger IPC - Messenger object contains binder to send to client
    private val mMessenger = Messenger(IncomingHandler())

    // Messenger IPC - Message Handler
    internal inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // Get message from client. Save recent connected client info.
            val receivedBundle = msg.data
            RecentClient.client = Client(
                receivedBundle.getString(PACKAGE_NAME),
                receivedBundle.getInt(PID).toString(),
                receivedBundle.getString(DATA),
                "Messenger"
            )

            // Send message to the client. The message contains server info
            val message = Message.obtain(this@IncomingHandler, 0)
            val bundle = Bundle()
            bundle.putInt(CONNECTION_COUNT, connectionCount)
            bundle.putInt(PID, Process.myPid())
            message.data = bundle
            // The service can save the msg.replyTo object as a local variable
            // so that it can send a message to the client at any time
            msg.replyTo.send(message)
        }
    }

    // AIDL IPC - Binder object to pass to the client
    private val aidlBinder = object : IIPCExample.Stub() {

        override fun getPid(): Int = Process.myPid()

        override fun getConnectionCount(): Int = IPCServerService.connectionCount

        override fun setDisplayedValue(packageName: String?, pid: Int, data: String?) {
            val clientData =
                if (data == null || TextUtils.isEmpty(data)) NOT_SENT
                else data

            // Get message from client. Save recent connected client info.
            RecentClient.client = Client(
                packageName ?: NOT_SENT,
                pid.toString(),
                clientData,
                "AIDL"
            )
        }
    }

    // Pass the binder object to clients so they can communicate with this service
    override fun onBind(intent: Intent?): IBinder? {
        connectionCount++
        // Choose which binder we need to return based on the type of IPC the client makes
        return when (intent?.action) {
            "aidlexample" -> aidlBinder
            "messengerexample" -> mMessenger.binder
            else -> null
        }
    }

    // A client has unbound from the service
    override fun onUnbind(intent: Intent?): Boolean {
        RecentClient.client = null
        return super.onUnbind(intent)
    }

}