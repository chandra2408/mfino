package io.scal.ambi.ui.notebooks.create

import android.app.Activity
import android.arch.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import io.scal.ambi.di.ViewModelKey
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.notebooks.list.NotebookData
import javax.inject.Named

@Module
internal abstract class CreateNotebookModule {

    @Binds
    abstract fun bindActivity(activity: CreateNotebookActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(CreateNotebookViewModel::class)
    abstract fun bindViewModel(viewModel: CreateNotebookViewModel): ViewModel

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
        fun provideNotebookData(activity: CreateNotebookActivity): NotebookData {
            val chatDescription = activity.intent.getSerializableExtra(CreateNotebookActivity.EXTRA_NOTEBOOK_DATA) as? NotebookData
            return chatDescription ?: NotebookData("","", emptyList(),"",null,"",false,false,false, emptyList())
        }

    }
}