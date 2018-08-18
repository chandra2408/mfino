package io.scal.ambi.ui.notebooks.list

import com.ambi.work.R
import com.ambi.work.databinding.ItemNotebookBinding
import io.scal.ambi.ui.home.notifications.NotebookListAdapterDelegateBase

internal class NotebookDelegate(viewModel: INotebookListViewModel) :
    NotebookListAdapterDelegateBase<ItemNotebookBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_notebook

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is NotebookData
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemNotebookBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? NotebookData
        super.onBindViewHolder(items, position, binding, payloads)
    }
}