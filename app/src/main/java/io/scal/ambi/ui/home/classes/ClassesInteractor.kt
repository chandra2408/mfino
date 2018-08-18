package io.scal.ambi.ui.home.classes

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class ClassesInteractor @Inject constructor(private val postsRepository: IClassesRepository,
                                            private val localUserDataRepository: ILocalUserDataRepository) : IClassesInteractor {

    private var currentUser: User? = null


    override fun loadCurrentUser(): Observable<User> =
            localUserDataRepository.observeCurrentUser().doOnNext { currentUser = it }

    override fun getAllClasses(): Single<List<ClassesData?>> {
        return postsRepository.getAllClasses()
    }

    override fun loadClasses(page: Int): Single<List<ClassesData>> =
            postsRepository.loadClasses(page.toLong() - 1)

}