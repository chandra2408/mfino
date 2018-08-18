package io.scal.ambi.ui.webview.viewer

import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
abstract class FileViewerModule {

    @Binds
    @IntoMap
    @ViewModelKey(FileViewerViewModel::class)
    abstract fun bindViewModel(viewModel: FileViewerViewModel): ViewModel

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Named("url")
        fun provideURL(activity: FileViewerActivity): String {
            val chatDescription = activity.intent.getSerializableExtra(FileViewerActivity.URL) as? String
            return chatDescription ?: ""
        }

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }

}