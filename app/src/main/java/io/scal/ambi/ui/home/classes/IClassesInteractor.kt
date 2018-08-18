package io.scal.ambi.ui.home.classes

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.user.User

/**
 * Created by chandra on 30-07-2018.
 */
interface IClassesInteractor {

    fun loadClasses(page: Int): Single<List<ClassesData>>

    fun getAllClasses(): Single<List<ClassesData?>>

    fun loadCurrentUser(): Observable<User>

}