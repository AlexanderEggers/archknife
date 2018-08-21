package archknife.context

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module class which is used to provide the context wrapper object to the project.
 *
 * @since 1.0.0
 */
@Module
class ContextProviderModule {

    /**
     * Provides the ContextProvider (context wrapper) to the project. The wrapper classes includes
     * the current active context object.
     *
     * @since 1.0.0
     */
    @Singleton
    @Provides
    fun provideActivityContextProvider(): ContextProvider {
        return ContextProvider
    }
}