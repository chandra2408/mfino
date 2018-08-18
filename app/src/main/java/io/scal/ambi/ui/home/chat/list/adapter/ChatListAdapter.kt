package io.scal.ambi.ui.home.chat.list.adapter

import android.support.v7.util.DiffUtil
import android.widget.Filter
import android.widget.Filterable
import com.ambi.work.R
import io.scal.ambi.extensions.binding.DefaultDiffCallback
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateStaticView
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated
import io.scal.ambi.ui.home.chat.list.ChatListViewModel
import io.scal.ambi.ui.home.chat.list.data.UIChatList

class ChatListAdapter(viewModel: ChatListViewModel) : RecyclerViewAdapterDelegated<Any>(),Filterable {

    private val footerElement = Any()
    var copyOfOrginalList: HeaderFooterList = HeaderFooterList(null, null, emptyList())

    override var dataList: List<Any> = HeaderFooterList(null, footerElement, emptyList())

    private var chatList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        addDelegate(ChatListAdapterItemDelegate(viewModel))
        addDelegate(AdapterDelegateStaticView(footerElement, R.layout.item_adapter_progress_footer))

        setHasStableIds(true)

        chatList.updateFooterVisibility(false, this)
    }

    override fun getItemId(position: Int): Long {
        val item = dataList[position]
        return when (item) {
            is UIChatList -> item.hashCode().toLong()
            footerElement -> "footer_0".hashCode().toLong()
            else          -> throw IllegalArgumentException("unknown item: $item")
        }
    }

    fun updateData(data: List<UIChatList>) {
        val oldMessageList = chatList
        chatList = chatList.copy(data = data)
        copyOfOrginalList = copyOfOrginalList.copy(data = data)

        val diffResult = DiffUtil.calculateDiff(DefaultDiffCallback(oldMessageList, chatList, true), true)
        diffResult.dispatchUpdatesTo(this)
    }

    fun releaseData() {
        chatList = chatList.copy(data = emptyList())
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        chatList.updateFooterVisibility(show, this)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    chatList = chatList?.copy(data = copyOfOrginalList)
                } else {
                    val filteringList = mutableListOf<Any>()

                    for (row in copyOfOrginalList) {

                        when(row){
                            is UIChatList -> if(row.newTitle?.toLowerCase()!!.contains(charString.toLowerCase())){
                                filteringList.add(row)
                            }
                        }
                    }
                    chatList = chatList?.copy(data = filteringList)
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = chatList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                chatList = chatList?.copy(filterResults.values)
                notifyDataSetChanged()
            }
        }
    }
}