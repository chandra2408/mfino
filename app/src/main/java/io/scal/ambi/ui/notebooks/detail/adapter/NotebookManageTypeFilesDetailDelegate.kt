package io.scal.ambi.ui.notebooks.detail.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ManageNotebookFilesItemBinding
import io.scal.ambi.entity.notebooks.FileData
import io.scal.ambi.ui.home.notifications.NotebookListAdapterDelegateBase
import io.scal.ambi.ui.notebooks.list.INotebookListViewModel

internal class NotebookManageTypeFilesDetailDelegate(viewModel: INotebookListViewModel) :
    NotebookListAdapterDelegateBase<ManageNotebookFilesItemBinding>(viewModel) {

    override val layoutId: Int = R.layout.manage_notebook_files_item

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is FileData
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ManageNotebookFilesItemBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? FileData
        super.onBindViewHolder(items, position, binding, payloads)
    }
}