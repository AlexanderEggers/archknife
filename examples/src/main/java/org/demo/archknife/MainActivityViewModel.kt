package org.demo.archknife

import androidx.lifecycle.ViewModel
import archknife.annotation.ProvideViewModel
import javax.inject.Inject

@ProvideViewModel
class MainActivityViewModel
@Inject constructor(testObject: TestObject) : ViewModel() {

    init {
        testObject.doSomething()
    }
}