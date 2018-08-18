package io.scal.ambi.ui.notebooks.list

import io.reactivex.Single
import io.scal.ambi.ui.notebooks.list.entity.NotebookItem

/**
 * Created by chandra on 10-08-2018.
 */
interface INotebookListInteractor {

    fun loadNotebooks(): Single<List<NotebookData>>
}