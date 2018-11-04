package org.demo.archknife

import android.app.IntentService
import android.content.Intent
import archknife.annotation.ProvideService

@ProvideService
class ServiceTest: IntentService("test.service"){

    override fun onHandleIntent(intent: Intent?) {
        //do nothing
    }
}