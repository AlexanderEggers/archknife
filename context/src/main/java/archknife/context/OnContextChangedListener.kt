package archknife.context

import android.content.Context

interface OnContextChangedListener {
    fun onContextChanged(newContext: Context?)
}