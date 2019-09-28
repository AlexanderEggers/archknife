package archknife.fragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * This module is using the EmptyFragment to provide a default module to any Activity.
 *
 * @since 1.0
 */
@Module
abstract class EmptyFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeEmptyFragment(): EmptyFragment
}