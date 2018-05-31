package archknife.context

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule {

    @Singleton
    @Provides
    fun provideActivityContextProvider(): ContextProvider {
        return ContextProvider
    }
}