package io.scal.ambi.ui.home.notifications

import com.ambi.work.R
import com.ambi.work.databinding.ItemContactBinding
import io.scal.ambi.model.data.server.responses.user.UsersDetails
import io.scal.ambi.ui.notebooks.contact.ItemSelectContactViewModel

internal class ContactDelegate(viewModel: ItemSelectContactViewModel) :
    ContactAdapterDelegateBase<ItemContactBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_contact

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is UsersDetails
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemContactBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? UsersDetails
        super.onBindViewHolder(items, position, binding, payloads)
    }
}