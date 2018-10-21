package archknife.context

import android.app.Activity
import android.content.Context
import java.lang.ref.WeakReference

/**
 * Context wrapper class that is saving the current context instance into a WeakReference.
 *
 * @since 1.0.0
 */
object ContextProvider {

    private var contextRef: WeakReference<Context?> = WeakReference(null)
    private val listenerList: ArrayList<OnContextChangedListener> = ArrayList()

    var context: Context?
        /**
         * Sets a new context instance.
         *
         * @since 1.0.0
         */
        set(context) {
            contextRef = WeakReference(context)

            listenerList.forEach {
                it.onContextChanged(context)
            }
        }
        /**
         * Returns the current context object.
         *
         * @since 1.0.0
         */
        get() = contextRef.get()

    val activity: Activity?

        /**
         * Returns the current context instance as an activity.
         *
         * @since 1.0.0
         */
        get() {
            val context = contextRef.get()
            return if(context != null && context is Activity) {
                context
            } else null
        }

    fun addListener(listener: OnContextChangedListener) {
        listenerList.add(listener)
    }
}