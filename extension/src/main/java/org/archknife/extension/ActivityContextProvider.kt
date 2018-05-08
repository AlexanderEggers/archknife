package org.archknife.extension

import android.content.Context
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityContextProvider @Inject constructor() {

    private var contextRef: WeakReference<Context?> = WeakReference(null)

    var context: Context?
        get() = contextRef.get()
        set(context) {
            this.contextRef = WeakReference(context)
        }
}