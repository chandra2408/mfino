package io.scal.ambi.ui.home.notifications

import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.widget.Filter
import android.widget.Filterable
import io.scal.ambi.ui.global.base.adapter.DataObserverForAdapter
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated
import io.scal.ambi.ui.notebooks.list.INotebookListViewModel
import io.scal.ambi.ui.notebooks.list.NotebookData
import io.scal.ambi.ui.notebooks.list.NotebookDelegate
import io.scal.ambi.ui.notebooks.list.NotebookListViewModel

/**
 * Created by chandra on 30-07-2018.
 */

class NotebookListAdapter(viewModel: INotebookListViewModel) : RecyclerViewAdapterDelegated<Any>(), Filterable{

    var copyOfOrginalList: HeaderFooterList = HeaderFooterList(null, null, emptyList())

    private var dataObserver: NotebookListAdapterDataObserver? = null

    private val hasHeader = viewModel is NotebookListViewModel
    override var dataList: List<Any> = HeaderFooterList(null,null,emptyList())
    private var NotificationList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        addDelegate(NotebookDelegate(viewModel))
        setHasStableIds(true)
        NotificationList.updateHeaderVisibility(true, this)
        NotificationList.updateFooterVisibility(false, this)
    }

    override fun getItemId(position: Int): Long {
        val item = NotificationList[position]
        return item.hashCode().toLong();
    }

    fun updateData(data: ObservableList<NotebookData>) {
        releaseData()
        NotificationList = NotificationList.copy(data = data)
        copyOfOrginalList = copyOfOrginalList.copy(data = data)
        dataObserver = NotebookListAdapterDataObserver(data, this, hasHeader)
        notifyDataSetChanged()
    }

    fun releaseData() {
        dataObserver?.release()
        NotificationList = NotificationList.copy(data = emptyList())
        dataObserver = null
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        NotificationList.updateFooterVisibility(show, this)
    }

    private class NotebookListAdapterDataObserver(data: ObservableList<NotebookData>, adapter: RecyclerView.Adapter<*>, private val hasHeader: Boolean) :
            DataObserverForAdapter<NotebookData>(data, adapter) {

        override fun notifyItemRangeChanged(adapter: RecyclerView.Adapter<*>, positionStart: Int, itemCount: Int) {
            super.notifyItemRangeChanged(adapter, positionStart + (if (hasHeader) 1 else 0), itemCount)
        }

        override fun notifyItemRangeInserted(adapter: RecyclerView.Adapter<*>, toPosition: Int, itemCount: Int) {
            super.notifyItemRangeInserted(adapter, toPosition + (if (hasHeader) 1 else 0), itemCount)
        }

        override fun notifyItemRangeRemoved(adapter: RecyclerView.Adapter<*>, fromPosition: Int, itemCount: Int) {
            super.notifyItemRangeRemoved(adapter, fromPosition + (if (hasHeader) 1 else 0), itemCount)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    NotificationList = NotificationList?.copy(data = copyOfOrginalList)
                } else {
                    val filteringList = mutableListOf<Any>()

                    for (row in copyOfOrginalList) {

                        when(row){
                            is NotebookData -> if(row.title?.toLowerCase()!!.contains(charString.toLowerCase())){
                                filteringList.add(row)
                            }
                        }
                    }
                    NotificationList = NotificationList?.copy(data = filteringList)
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = NotificationList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                NotificationList = NotificationList?.copy(filterResults.values)
                notifyDataSetChanged()
            }
        }
    }
}