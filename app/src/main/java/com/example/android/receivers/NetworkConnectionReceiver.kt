package com.example.android.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android.utils.NetworkUtils


class NetworkConnectionReceiver : BroadcastReceiver() {

    companion object {
        const val CONNECTION_RECEIVER_ACTION = "action_network_receiver"
        const val STATUS = "network_status"
        var isFirstTime = true
    }

    override fun onReceive(context: Context, intent: Intent) {

        val localBroadcastManager = LocalBroadcastManager.getInstance(context)

        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        when (intent.action) {

            "android.net.conn.CONNECTIVITY_CHANGE" -> {
                if (!isFirstTime) {
                    val localIntent = Intent(CONNECTION_RECEIVER_ACTION)
                    localIntent.putExtra(STATUS, NetworkUtils.isConnected(context))
                    localBroadcastManager.sendBroadcast(localIntent)
                } else {
                    isFirstTime = false
                }
            }
        }
    }
}
