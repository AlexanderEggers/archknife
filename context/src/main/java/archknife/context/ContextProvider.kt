package archknife.context

import android.app.Activity
import android.content.Context
import java.lang.ref.WeakReference

/**
 * Context wrapper class that is saving the current activityContext instance into a WeakReference.
 *
 * @since 1.0.0
 */
object ContextProvider {

    private var activityContextRef: WeakReference<Context?> = WeakReference(null)
    private var applicationContextRef: WeakReference<Context?> = WeakReference(null)

    var activityContext: Context?
        /**
         * Sets a new activityContext instance.
         *
         * @since 1.0.0
         */
        set(context) { activityContextRef = WeakReference(context) }
        /**
         * Returns the current activityContext object.
         *
         * @since 1.0.0
         */
        get() = activityContextRef.get()

    val activity: Activity?

        /**
         * Returns the current activityContext instance as an activity.
         *
         * @since 1.0.0
         */
        get() {
            val context = activityContextRef.get()
            return if(context != null && context is Activity) {
                context
            } else null
        }

    var applicationContext: Context?
        /**
         * Sets a new applicationContext instance.
         *
         * @since 1.0.0
         */
        set(context) { applicationContextRef = WeakReference(context) }
        /**
         * Returns the current applicationContext object.
         *
         * @since 1.0.0
         */
        get() = applicationContextRef.get()
}