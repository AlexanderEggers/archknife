package org.demo.archknife

import android.support.v4.app.Fragment
import archknife.annotation.ProvideFragment

@ProvideFragment([MainActivity::class])
class MainFragment : Fragment()
