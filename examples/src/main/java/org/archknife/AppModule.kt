package org.archknife

import dagger.Module
import dagger.Provides
import org.archknife.annotation.ProvideModule
import javax.inject.Singleton

@ProvideModule
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideTestObject(): TestObject {
        return TestObject()
    }
}