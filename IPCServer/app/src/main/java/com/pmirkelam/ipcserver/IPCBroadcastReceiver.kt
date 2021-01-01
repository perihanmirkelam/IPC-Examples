package com.pmirkelam.ipcserver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class IPCBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        RecentClient.client = Client(
            intent?.getStringExtra(PACKAGE_NAME),
            intent?.getStringExtra(PID),
            intent?.getStringExtra(DATA),
            "Broadcast"
        )
    }
}