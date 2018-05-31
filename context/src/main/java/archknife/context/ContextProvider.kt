package archknife.context

import android.content.Context
import java.lang.ref.WeakReference

object ContextProvider {

    private var contextRef: WeakReference<Context?> = WeakReference(null)

    var context: Context?
        get() = contextRef.get()
        set(context) {
            contextRef = WeakReference(context)
        }
}