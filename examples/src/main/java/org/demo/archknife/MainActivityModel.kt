package org.demo.archknife

import android.arch.lifecycle.ViewModel
import org.archknife.annotation.ProvideViewModel
import javax.inject.Inject

@ProvideViewModel
class MainActivityModel
@Inject constructor(testObject: TestObject) : ViewModel() {

    init {
        testObject.doSomething()
    }
}