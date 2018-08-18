package io.scal.ambi.ui.notebooks.list

import android.app.Activity
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.ui.global.base.BetterRouter
import javax.inject.Named

@Module
internal abstract class NotebookListModule {

    @Binds
    abstract fun bindActivity(activity: NotebookListActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(NotebookListViewModel::class)
    abstract fun bindViewModel(viewModel: NotebookListViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: NotebookListInteractor): INotebookListInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }

        @JvmStatic
        @Provides
        @Named("userId")
        fun provideUserId(activity: NotebookListActivity): String {
            val allChatDescriptions = activity.intent.getStringExtra(NotebookListActivity.EXTRA_USER_ID)
            return allChatDescriptions
        }
    }
}