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

    var context: Context?
        /**
         * Sets a new context instance.
         *
         * @since 1.0.0
         */
        set(context) {
            contextRef = WeakReference(context)
        }
        /**
         * Returns the current context object.
         *
         * @since 1.0.0
         */
        get() = contextRef.get()

    /**
     * Returns the current context instance as an activity.
     *
     * @since 1.0.0
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Activity> getActivity(): T? {
        return contextRef.get() as T?
    }
}