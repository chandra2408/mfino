package io.scal.ambi.ui.notebooks.list

import android.app.Activity.RESULT_OK
import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Single
import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import io.scal.ambi.ui.global.model.PaginatorStateViewController
import io.scal.ambi.ui.global.model.createPaginator
import ru.terrakok.cicerone.result.ResultListener
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by chandra on 10-08-2018.
 */
class NotebookListViewModel @Inject internal constructor(private val context: Context,
                                                         router: BetterRouter,
                                                         private val interactor: INotebookListInteractor,
                                                         private val rxSchedulersAbs: RxSchedulersAbs,
                                                         val searchViewModel: NotebookListSearchViewModel,
                                                         @Named("userId") val userId: String) :
        BaseViewModel(router), INotebookListViewModel {

    internal val progressState = ObservableField<NotebookListState>(NotebookListState.TotalProgress)
    internal val errorState = ObservableField<NotebookListErrorState>()
    val dataState = ObservableField<NotebookListDataState>()


    private val paginator = createPaginator(
            { page -> executeLoadNextPage(page) },
            object :
                    PaginatorStateViewController<NotebookData, NotebookListState, NotebookListErrorState>(context, progressState, errorState) {

                override fun generateProgressEmptyState() = NotebookListState.EmptyProgress
                override fun generateProgressNoState() = NotebookListState.NoProgress
                override fun generateProgressRefreshState() = NotebookListState.RefreshProgress
                override fun generateProgressPageState() = NotebookListState.PageProgress

                override fun generateErrorFatal(message: String) = NotebookListErrorState.FatalErrorState(message)
                override fun generateErrorNonFatal(message: String) = NotebookListErrorState.NonFatalErrorState(message)
                override fun generateErrorNo() = NotebookListErrorState.NoErrorState

                override fun showEmptyView(show: Boolean) {
                    if (show) dataState.set(NotebookListDataState.NotebookListEmpty(dataState.get()?.profileInfo))
                }

                override fun showData(show: Boolean, data: List<NotebookData>) {
                    if (show) {
                        dataState.set(NotebookListDataState.NotebookListData(dataState.get()?.profileInfo, OptimizedObservableArrayList(data)))
                    }
                }
            },
            true, true
    )

    private fun executeLoadNextPage(page: Int): Single<List<NotebookData>> {
        return interactor
                .loadNotebooks()
                .subscribeOn(rxSchedulersAbs.ioScheduler)
                .observeOn(rxSchedulersAbs.computationScheduler)
                .observeOn(rxSchedulersAbs.mainThreadScheduler)
    }

    fun retry() {
        refresh()
    }


    fun refresh() {
        paginator.refresh()
    }

    fun loadNextPage() {
        paginator.loadNewPage()
    }

    fun init(){
        paginator.activate()
        paginator.forceRefresh()
    }

    fun createNewNotebook() = router.navigateTo(NavigateTo.CREATE_NOTEBOOK)

    private val channelSelectionListener = ResultListener { resultData ->
        if(resultData == RESULT_OK){
            paginator.refresh()
        }
    }

    init{
        router.setResultListener(ResultCodes.CREATE_NOTEBOOK, channelSelectionListener)
    }

    override fun openNotebookDetails(element: NotebookData) {
        router.navigateTo(NavigateTo.OPEN_NOTEBOOK_DETAILS,element)
    }

    override fun viewFile(element: FileData) {
        //nothing
    }

}