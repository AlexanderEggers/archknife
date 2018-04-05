package org.archknife.fragment

import android.support.v4.app.Fragment
import org.archknife.Demo1Activity
import org.archknife.annotation.ProvideFragment
import org.archknife.support.Injectable

@ProvideFragment(Demo1Activity::class)
class FragmentTest1: Fragment(), Injectable