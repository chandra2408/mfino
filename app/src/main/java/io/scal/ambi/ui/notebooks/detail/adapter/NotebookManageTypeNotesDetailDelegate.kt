package io.scal.ambi.ui.notebooks.list

import com.ambi.work.R
import com.ambi.work.databinding.ManageNotebookNotesItemBinding
import io.scal.ambi.model.data.server.responses.notebooks.NotesItem
import io.scal.ambi.ui.home.notifications.NotebookListAdapterDelegateBase

internal class NotebookManageTypeNotesDetailDelegate(viewModel: INotebookListViewModel) :
    NotebookListAdapterDelegateBase<ManageNotebookNotesItemBinding>(viewModel) {

    override val layoutId: Int = R.layout.manage_notebook_notes_item

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is NotesItem
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ManageNotebookNotesItemBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? NotesItem
        super.onBindViewHolder(items, position, binding, payloads)
    }
}