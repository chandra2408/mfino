package io.scal.ambi.ui.notebooks.contact

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
internal abstract class SelectContactModule {

    @Binds
    abstract fun bindActivity(activity: ContactPickerActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(SelectContactViewModel::class)
    abstract fun bindViewModel(viewModel: SelectContactViewModel): ViewModel

    @Binds
    abstract fun bindInteractor(interactor: SelectContactInteractor): ISelectContactInteractor

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun provideLocalNavigation(@Named("rootRouter") router: BetterRouter): BetterRouter {
            return router
        }
    }
}