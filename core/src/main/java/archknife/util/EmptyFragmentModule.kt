package archknife.util

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class EmptyFragmentModule {

    @ContributesAndroidInjector
    internal abstract fun contributeEmptyFragment(): EmptyFragment
}