package archknife.context

import android.app.Activity
import android.content.Context

interface ContextProviderCommunicator {

    var activityContext: Context?

    val activity: Activity?

    var applicationContext: Context?
}