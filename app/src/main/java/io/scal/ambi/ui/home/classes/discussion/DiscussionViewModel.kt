package io.scal.ambi.ui.home.classes.discussion

import android.content.Context
import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.extensions.binding.observable.OptimizedObservableArrayList
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.interactor.home.newsfeed.INewsFeedInteractor
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage
import io.scal.ambi.ui.global.model.DynamicUserChoicer
import io.scal.ambi.ui.global.model.PaginatorStateViewController
import io.scal.ambi.ui.global.model.createPaginator
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.home.newsfeed.list.*
import io.scal.ambi.ui.home.newsfeed.list.adapter.INewsFeedViewModel
import io.scal.ambi.ui.home.newsfeed.list.data.UIModelFeed
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by chandra on 16-08-2018.
 */
class DiscussionViewModel @Inject constructor(private val context: Context,
                                              router: BetterRouter,
                                              private val interactor: INewsFeedInteractor,
                                              rxSchedulersAbs: RxSchedulersAbs,
                                              @Named("aboutData") val memberdata: ClassesData) : BaseUserViewModel(router,{interactor.loadCurrentUser()},rxSchedulersAbs),
        INewsFeedViewModel {

    internal val progressState = ObservableField<NewsFeedProgressState>()
    internal val errorState = ObservableField<NewsFeedErrorState>()
    internal val dataState = ObservableField<NewsFeedDataState>()


    private val paginator = createPaginator(
            { page -> executeLoadNextPage(page.toLong()) },
            object : PaginatorStateViewController<UIModelFeed, NewsFeedProgressState, NewsFeedErrorState>(context, progressState, errorState) {

                override fun generateProgressEmptyState() = NewsFeedProgressState.EmptyProgress
                override fun generateProgressNoState() = NewsFeedProgressState.NoProgress
                override fun generateProgressRefreshState() = NewsFeedProgressState.RefreshProgress
                override fun generateProgressPageState() = NewsFeedProgressState.PageProgress

                override fun generateErrorFatal(message: String) = NewsFeedErrorState.FatalErrorState(message)
                override fun generateErrorNonFatal(message: String) = NewsFeedErrorState.NonFatalErrorState(message)
                override fun generateErrorNo() = NewsFeedErrorState.NoErrorState

                override fun showEmptyView(show: Boolean) {
                    if (show) dataState.set(NewsFeedDataState.Empty)
                }

                override fun showData(show: Boolean, data: List<UIModelFeed>) {
                    if (show) {
                        dataState.set(NewsFeedDataState.Data(OptimizedObservableArrayList(data)))
                    }
                }
            },
            true
    )

    private fun executeLoadNextPage(page: Long): Single<List<UIModelFeed>> {
        return interactor
                .getDiscussions("class",page,memberdata._id)
                .subscribeOn(rxSchedulersAbs.ioScheduler)
                .observeOn(rxSchedulersAbs.computationScheduler)
                .flatMap {
                    Observable.fromIterable(it)
                            .map<UIModelFeed> { it.toNewsFeedElement(currentUser.get()) }
                            .toList()
                }
                .observeOn(rxSchedulersAbs.mainThreadScheduler)
    }

    fun refresh() {
        paginator.refresh()
    }

    fun loadNextPage() {
        paginator.loadNewPage()
    }

    override fun onCurrentUserFetched(user: User) {
        super.onCurrentUserFetched(user)
        onInit()
    }

    private fun onInit() {
        paginator.activate()
        paginator.refresh()
    }

    override fun onCleared() {
        paginator.release()
        super.onCleared()
    }


    override fun openAuthorOf(element: UIModelFeed) {
        userActions.openAuthorOf(element)
    }

    override fun openCommentsOf(element: UIModelFeed) {
        userActions.openCommentsOf(element)
    }

    override fun changeUserLikeOf(element: UIModelFeed) {
        val currentDataState = dataState.get()
        if (currentDataState is NewsFeedDataState.Data) {
            userActions.changeUserLikeOf(currentDataState.newsFeed, element, currentUser.get())
        }
    }

    override fun sendCommentForElement(element: UIModelFeed) {
        userActions.sendCommentForElement(element)
    }

    override fun selectPollChoice(element: UIModelFeed.Poll, choice: UIModelFeed.Poll.PollChoiceResult) {
        val currentDataState = dataState.get()
        if (currentDataState is NewsFeedDataState.Data) {
            userActions.selectPollChoice(currentDataState.newsFeed, element, choice, currentUser.get())
        }
    }


    private val userActions = NewsFeedViewModelActions(router,
            { uiModelFeed, action ->
                interactor
                        .changeUserLikeForPost(uiModelFeed.feedItem, action == DynamicUserChoicer.Action.LIKE)
            },
            { uiModelFeed, commentTet ->
                interactor.sendUserCommentToPost(uiModelFeed.feedItem, commentTet)
                        .doOnError {
                            handleError(it)

                            errorState.set(NewsFeedErrorState.NonFatalErrorState(it.toGoodUserMessage(context)))
                            errorState.set(NewsFeedErrorState.NoErrorState)
                        }
            },
            { uiModelFeed, pollChoiceResult ->
                interactor.answerForPoll(uiModelFeed.feedItem, pollChoiceResult.pollChoice)
                        .doOnError {
                            handleError(it)

                            errorState.set(NewsFeedErrorState.NonFatalErrorState(it.toGoodUserMessage(context)))
                            errorState.set(NewsFeedErrorState.NoErrorState)
                        }
            },
            {
                val currentState = dataState.get()
                if (currentState is NewsFeedDataState.Data) {
                    currentState.newsFeed
                } else {
                    null
                }
            },
            rxSchedulersAbs)

    fun createStatus() {
        router.navigateTo(NavigateTo.CREATE_STATUS)
    }

}
