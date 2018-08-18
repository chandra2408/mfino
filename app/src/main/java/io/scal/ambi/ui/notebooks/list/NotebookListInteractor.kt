package io.scal.ambi.ui.notebooks.list

import io.reactivex.Single
import io.scal.ambi.model.repository.data.newsfeed.INotebooksRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import javax.inject.Inject

class NotebookListInteractor @Inject constructor(private val postsRepository: INotebooksRepository,
                                                 private val localUserDataRepository: ILocalUserDataRepository) : INotebookListInteractor {

    override fun loadNotebooks(): Single<List<NotebookData>> {
        return postsRepository.loadNotebooks()
    }

}