package io.scal.ambi.ui.home.chat.details.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemChatDetailsMessageImageUrlBinding
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.home.chat.details.ChatDetailsViewModel
import io.scal.ambi.ui.home.chat.details.data.UIChatMessage

internal class ChatDetailsImageURLMessage(private val viewModel: ChatDetailsViewModel) : AdapterDelegateBase<ItemChatDetailsMessageImageUrlBinding, List<Any>>() {

    override val layoutId: Int = R.layout.item_chat_details_message_image_url

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        items[position] is UIChatMessage.URLMessage

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemChatDetailsMessageImageUrlBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as UIChatMessage.URLMessage
        binding.viewModel = viewModel
    }
}