package org.archknife

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import org.archknife.annotation.ProvideActivity
import dagger.android.DispatchingAndroidInjector
import org.archknife.extension.ViewModelFactory
import javax.inject.Inject

@ProvideActivity
class Demo1Activity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var viewModelTest: ViewModelTest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelTest = ViewModelProviders.of(this, viewModelFactory).get(ViewModelTest::class.java)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}