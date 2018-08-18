package io.scal.ambi.ui.notebooks.contact

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.responses.user.UsersDetails
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class SelectContactInteractor @Inject constructor(private val postsRepository: IUserRepository,
                                                  private val localUserDataRepository: ILocalUserDataRepository) : ISelectContactInteractor {

    private var currentUser: User? = null

    override fun loadCurrentUser(): Observable<User> {
        return localUserDataRepository.observeCurrentUser().doOnNext { currentUser = it }
    }

    override fun getAllUsers(): Single<List<UsersDetails>> {
        return postsRepository.getAllUsers()
    }

}