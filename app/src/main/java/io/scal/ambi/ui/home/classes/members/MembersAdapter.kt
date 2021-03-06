package io.scal.ambi.ui.home.classes.members

import android.widget.Filter
import android.widget.Filterable
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated
import io.scal.ambi.ui.home.classes.about.MembersData

/**
 * Created by chandra on 30-07-2018.
 */

class MembersAdapter(viewModel: IMembersViewModel) : RecyclerViewAdapterDelegated<Any>(), Filterable{

    var copyOfOrginalList: HeaderFooterList = HeaderFooterList(null, null, emptyList())

    override var dataList: List<Any> = HeaderFooterList(null, null, emptyList())

    private var NotificationList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        addDelegate(MembersDelegate(viewModel))
        addDelegate(HeaderDelegate(viewModel))
        addDelegate(HeaderSecondaryDelegate(viewModel))
        setHasStableIds(true)
        NotificationList.updateHeaderVisibility(true, this)
        NotificationList.updateFooterVisibility(false, this)
    }

    override fun getItemId(position: Int): Long {
        val item = NotificationList[position]
        return item.hashCode().toLong();
    }

    fun updateData(data: List<Any>) {
        releaseData()
        NotificationList = NotificationList.copy(data = data)
        copyOfOrginalList = copyOfOrginalList.copy(data = data)
        notifyDataSetChanged()
    }

    fun releaseData() {
        NotificationList = NotificationList.copy(data = emptyList())
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        NotificationList.updateFooterVisibility(show, this)
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
                            is MembersData -> if(row.name?.toLowerCase()!!.contains(charString.toLowerCase())){
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