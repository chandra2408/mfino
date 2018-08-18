package io.scal.ambi.ui.notebooks.contact

import io.reactivex.Observable
import io.reactivex.Single
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.responses.user.UsersDetails

interface ISelectContactInteractor {

    fun getAllUsers(): Single<List<UsersDetails>>

    fun loadCurrentUser(): Observable<User>
}