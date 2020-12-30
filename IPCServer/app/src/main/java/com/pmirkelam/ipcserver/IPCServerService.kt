package com.pmirkelam.ipcserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import android.text.TextUtils

class IPCServerService : Service() {

    companion object {
        var connectionCount: Int = 0
        val NOT_SENT = "Not sent!"
    }

    private val binder = object : IIPCExample.Stub() {

        override fun getPid(): Int = Process.myPid()

        override fun getConnectionCount(): Int = IPCServerService.connectionCount

        override fun setDisplayedValue(packageName: String?, pid: Int, data: String?) {
            val clientData =
                if (data == null || TextUtils.isEmpty(data)) NOT_SENT
                else data

            RecentClient.client = Client(
                packageName ?: NOT_SENT,
                pid.toString(),
                clientData,
                "AIDL"
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        connectionCount++
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        RecentClient.client = null
        return super.onUnbind(intent)
    }

}