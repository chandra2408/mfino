package io.scal.ambi.ui.global.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

class AutoScrollRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {

    private var emptyView: View? = null

    private val scrollObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)

            if (layoutManager !is LinearLayoutManager) {
                return
            }

            if (0 == positionStart) {
                if (0 == (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()) {
                    // we insert into the beginning
                    scrollToPosition(0)
                }
            } else if (positionStart + itemCount == adapter?.itemCount) {
                val lastVisibleItemPosition = (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                // strange check... but it works good
                if (lastVisibleItemPosition in positionStart - 1..positionStart + 1) {
                    scrollToPosition(positionStart + 1)
                }
            }
        }

        override fun onChanged() {
            super.onChanged()
            toggleEmptyView()
        }
    }

    private fun toggleEmptyView(){
        val adapter = adapter
        if (adapter != null && emptyView != null) {
            if (adapter.itemCount == 0) {
                emptyView!!.setVisibility(View.VISIBLE)
                this@AutoScrollRecyclerView.setVisibility(View.GONE)
            } else {
                emptyView!!.setVisibility(View.GONE)
                this@AutoScrollRecyclerView.setVisibility(View.VISIBLE)
            }
        }
    }

    private val emptyObserver = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            toggleEmptyView()
        }
    }

    override fun setAdapter(newAdapter: Adapter<*>?) {
        adapter?.unregisterAdapterDataObserver(scrollObserver)
        super.setAdapter(newAdapter)
        newAdapter?.registerAdapterDataObserver(scrollObserver)
        if(this.emptyView!=null){
            newAdapter?.registerAdapterDataObserver(emptyObserver);
        }
    }

    fun setEmptyView(emptyView: View) {
        this.emptyView = emptyView
    }
}