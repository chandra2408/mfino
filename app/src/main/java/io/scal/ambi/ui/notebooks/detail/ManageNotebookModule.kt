package io.scal.ambi.ui.notebooks.detail

import android.app.Activity
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.notebooks.create.CreateNotebookInteractor
import io.scal.ambi.ui.notebooks.create.ICreateOrDeleteNotebookInteractor
import io.scal.ambi.ui.notebooks.list.NotebookData
import io.scal.ambi.ui.notebooks.list.NotebookListActivity
import javax.inject.Named

@Module
internal abstract class ManageNotebookModule {

    @Binds
    abstract fun bindActivity(activity: NotebookListActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(ManageNotebookViewModel::class)
    abstract fun bindViewModel(viewModel: ManageNotebookViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: CreateNotebookInteractor): ICreateOrDeleteNotebookInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }

        @JvmStatic
        @Provides
        @Named("notebookData")
        fun provideChatUid(activity: ManageNotebookActivity): NotebookData {
            val chatDescription = activity.intent.getSerializableExtra(ManageNotebookActivity.EXTRA_NOTEBOOK_DATA) as? NotebookData
            if (null == chatDescription) {
                activity.finish()
            }
            return chatDescription ?: NotebookData("","", emptyList(),"",null,"",false,false,false, emptyList())
        }

    }
}