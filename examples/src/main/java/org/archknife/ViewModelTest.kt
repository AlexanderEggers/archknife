package org.archknife

import android.arch.lifecycle.ViewModel
import org.archknife.annotation.ProvideViewModel
import javax.inject.Inject

@ProvideViewModel
class ViewModelTest @Inject constructor(): ViewModel()
