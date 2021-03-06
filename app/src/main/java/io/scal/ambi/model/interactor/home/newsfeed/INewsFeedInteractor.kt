package io.scal.ambi.model.interactor.home.newsfeed

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.actions.Comment
import io.scal.ambi.entity.feed.Audience
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.feed.NewsFeedItemPoll
import io.scal.ambi.entity.feed.PollChoice
import io.scal.ambi.entity.user.User
import org.joda.time.DateTime

interface INewsFeedInteractor {

    fun loadLatestNews(): Single<List<NewsFeedItem>>

    fun loadCurrentUser(): Observable<User>

    fun loadNewsFeedPage(entityType: String, page: Int, audience: Audience): Single<List<NewsFeedItem>>

    fun changeUserLikeForPost(feedItem: NewsFeedItem, like: Boolean): Completable

    fun answerForPoll(feedItemPoll: NewsFeedItemPoll, pollChoice: PollChoice): Single<NewsFeedItem>

    fun sendUserCommentToPost(newsFeedItem: NewsFeedItem, userCommentText: String): Single<Comment>

    fun getDiscussions(entityType: String, page: Long, entityId: String): Single<List<NewsFeedItem?>>
}