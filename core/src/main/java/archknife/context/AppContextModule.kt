package archknife.context

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppContextModule {

    @Singleton
    @Provides
    fun provideAppContext(app: Application): Context {
        return app.applicationContext
    }
}