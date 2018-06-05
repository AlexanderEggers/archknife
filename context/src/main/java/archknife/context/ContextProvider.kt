package archknife.context

import android.content.Context
import android.support.v7.app.AppCompatActivity
import java.lang.ref.WeakReference

object ContextProvider {

    private var contextRef: WeakReference<Context?> = WeakReference(null)

    var context: Context?
        get() = contextRef.get()
        set(context) {
            contextRef = WeakReference(context)
        }

    @Suppress("UNCHECKED_CAST")
    fun <T: AppCompatActivity> getActivity(): T? {
        return contextRef.get() as T?
    }
}