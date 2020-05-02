package org.demo.archknife

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import archknife.annotation.ProvideFragment
import javax.inject.Inject

@ProvideFragment(activityClasses = [MainActivity::class])
class MainFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mainViewViewModel: MainActivityViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainViewViewModel = ViewModelProvider(viewModelStore, viewModelFactory)
                .get(MainActivityViewModel::class.java)
    }
}
