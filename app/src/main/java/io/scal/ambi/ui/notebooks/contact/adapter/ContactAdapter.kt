package io.scal.ambi.ui.home.notifications

import android.widget.Filter
import android.widget.Filterable
import io.scal.ambi.entity.user.User
import io.scal.ambi.model.data.server.responses.user.ItemUser
import io.scal.ambi.model.data.server.responses.user.UsersDetails
import io.scal.ambi.ui.global.base.adapter.HeaderFooterList
import io.scal.ambi.ui.global.base.adapter.RecyclerViewAdapterDelegated
import io.scal.ambi.ui.notebooks.contact.ItemSelectContactViewModel

/**
 * Created by chandra on 30-07-2018.
 */

class ContactAdapter(viewModel: ItemSelectContactViewModel) : RecyclerViewAdapterDelegated<Any>(), Filterable{

    var copyOfOrginalList: HeaderFooterList = HeaderFooterList(null, null, emptyList())

    override var dataList: List<Any> = HeaderFooterList(null,null,emptyList())
    private var originalContactList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        addDelegate(ContactDelegate(viewModel))
        setHasStableIds(true)
        originalContactList.updateHeaderVisibility(true, this)
        originalContactList.updateFooterVisibility(false, this)
    }

    override fun getItemId(position: Int): Long {
        val item = originalContactList[position]
        return item.hashCode().toLong();
    }

    fun updateData(data: List<UsersDetails>) {
        releaseData()
        originalContactList = originalContactList.copy(data = data)
        copyOfOrginalList = copyOfOrginalList.copy(data = data)
        notifyDataSetChanged()
    }

    fun updateItem(data: UsersDetails) {
        for(s:Any in originalContactList){
            when(s){
                is User ->{
                    if(s.uid == data._id){
                        s.isContactSelected = data.isContactSelected
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    fun getSelectedContacts(): List<UsersDetails>{
        return dataList.filter {
            it is UsersDetails
        }.toList().filter {
            it ->
            (it as UsersDetails).isContactSelected
        }.toList() as List<UsersDetails>
    }

    fun releaseData() {
        originalContactList = originalContactList.copy(data = emptyList())
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        originalContactList.updateFooterVisibility(show, this)
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    originalContactList = originalContactList?.copy(data = copyOfOrginalList)
                } else {
                    val filteringList = mutableListOf<Any>()

                    for (row in copyOfOrginalList) {

                        when(row){
                            is ItemUser -> if(row.name?.toLowerCase()!!.contains(charString.toLowerCase())){
                                filteringList.add(row)
                            }
                        }
                    }
                    originalContactList = originalContactList?.copy(data = filteringList)
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = originalContactList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                originalContactList = originalContactList?.copy(filterResults.values)
                notifyDataSetChanged()
            }
        }
    }

}