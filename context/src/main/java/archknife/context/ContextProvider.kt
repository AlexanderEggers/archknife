package archknife.context

import android.content.Context
import android.support.v7.app.AppCompatActivity
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
         * Returns the current context object.
         *
         * @since 1.0.0
         */
        get() = contextRef.get()

        /**
         * Sets a new context instance.
         *
         * @since 1.0.0
         */
        set(context) {
            contextRef = WeakReference(context)
        }

    /**
     * Returns the current context instance as an activity.
     *
     * @since 1.0.0
     */
    @Suppress("UNCHECKED_CAST")
    fun <T: AppCompatActivity> getActivity(): T? {
        return contextRef.get() as T?
    }
}