package archknife.fragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class EmptyFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeEmptyFragment(): EmptyFragment
}