package io.scal.ambi.ui.notebooks.list

import io.scal.ambi.entity.notebooks.FileData


/**
 * Created by chandra on 30-07-2018.
 */
interface INotebookListViewModel {

    fun openNotebookDetails(element: NotebookData)

    fun viewFile(element: FileData)

}