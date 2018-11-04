package org.demo.archknife

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import archknife.annotation.ProvideBroadcastReceiver

@ProvideBroadcastReceiver
class BroadcastReceiverTest: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //do nothing
    }
}