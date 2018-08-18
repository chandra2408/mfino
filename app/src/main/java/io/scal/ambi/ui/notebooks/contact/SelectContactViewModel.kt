package io.scal.ambi.ui.notebooks.contact

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.data.server.responses.user.UsersDetails
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.model.PaginatorStateViewController
import io.scal.ambi.ui.global.model.createPaginator
import javax.inject.Inject

class SelectContactViewModel @Inject internal constructor(private val context: Context,
                                                          router: BetterRouter,
                                                          private val interactor: ISelectContactInteractor,
                                                          rxSchedulersAbs: RxSchedulersAbs,
                                                          val searchViewModel: SelectContactSearchViewModel) : BaseUserViewModel(router,{interactor.loadCurrentUser()},rxSchedulersAbs), ItemSelectContactViewModel {

    internal val progressState = ObservableField<SelectContactState>(SelectContactState.TotalProgress)
    internal val errorState = ObservableField<SelectContactErrorState>()
    val dataState = ObservableField<SelectContactDataState>()
    val contactSelectorState = ObservableField<UsersDetails>()

    private val paginator = createPaginator(
            { page -> executeLoadNextPage(page) },
            object :
                    PaginatorStateViewController<UsersDetails, SelectContactState, SelectContactErrorState>(context, progressState, errorState) {

                override fun generateProgressEmptyState() = SelectContactState.EmptyProgress
                override fun generateProgressNoState() = SelectContactState.NoProgress
                override fun generateProgressRefreshState() = SelectContactState.RefreshProgress
                override fun generateProgressPageState() = SelectContactState.PageProgress

                override fun generateErrorFatal(message: String) = SelectContactErrorState.FatalErrorState(message)
                override fun generateErrorNonFatal(message: String) = SelectContactErrorState.NonFatalErrorState(message)
                override fun generateErrorNo() = SelectContactErrorState.NoErrorState

                override fun showEmptyView(show: Boolean) {
                    if (show) dataState.set(SelectContactDataState.SelectContactEmpty(dataState.get()?.profileInfo))
                }

                override fun showData(show: Boolean, data: List<UsersDetails>) {
                    if (show) {
                        dataState.set(SelectContactDataState.SelectContactData(dataState.get()?.profileInfo, OptimizedObservableArrayList(data)))
                    }
                }
            },
            true, true
    )

    fun retry() {
        refresh()
    }

    private fun executeLoadNextPage(page: Int): Single<List<UsersDetails>> {
        return interactor
                .getAllUsers()
                .subscribeOn(rxSchedulersAbs.ioScheduler)
                .observeOn(rxSchedulersAbs.computationScheduler)
                .observeOn(rxSchedulersAbs.mainThreadScheduler)
    }

    fun refresh() {
        paginator.refresh()
    }

    fun loadNextPage() {
        paginator.loadNewPage()
    }

    override fun changeContactSelection(element: UsersDetails) {
        element.isContactSelected = !element.isContactSelected
        contactSelectorState.set(element)
    }

    override fun onCurrentUserFetched(user: User) {
        super.onCurrentUserFetched(user)
        paginator.activate()
        paginator.forceRefresh()
    }

}