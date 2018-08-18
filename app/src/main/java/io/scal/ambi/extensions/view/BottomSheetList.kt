package io.scal.ambi.extensions.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import com.ambi.work.R


/**
 * Created by chandra on 06-08-2018.
 */
class BottomSheetList: RelativeLayout{
    private var tabXmlResource: Int = 0
    private var listItemLayout: Int = 0
    private var title: String? = null;
    var rootView = null
    var actionList: List<String> = mutableListOf()
    lateinit var listView: ListView;
    var listener: BottomSheetItemListener? = null
    lateinit var tvTitle: TextView;

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialise(attrs,0,context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialise(attrs,defStyleAttr,context)
    }

    private fun initialise(attrs: AttributeSet, defStyleAttr: Int, context: Context){
        val ta = context.theme .obtainStyledAttributes(attrs, R.styleable.BottomSheetList, defStyleAttr, 0)
        try
        {
            tabXmlResource = ta.getResourceId(R.styleable.BottomSheetList_bs_listXmlResource, 0)
            listItemLayout = ta.getResourceId(R.styleable.BottomSheetList_bs_listItemLayout, 0)
            title = ta.getString(R.styleable.BottomSheetList_bs_title)
        }
        finally
        {
            ta.recycle()
        }


        var inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.bottom_sheet,this,true)
        tvTitle = (findViewById<TextView>(R.id.tvTitle) as TextView)
        tvTitle.setText(title)
        initRecyclerView()
    }

    private fun initRecyclerView(){
        listView = (findViewById<View>(R.id.rcv_list) as ListView)
        listView.adapter = BottomSheetListAdapter(actionList, context)

        listView.onItemClickListener = OnItemClickListener { arg0, arg1, position, arg3
            ->
            if (listener != null) {
                listener?.selectedItem(actionList.get(position))
            }
        }
    }

    public fun setActionsList(actionList: List<String>, listener: BottomSheetItemListener? = null){
        this.actionList = actionList
        this.listener = listener;
        listView.adapter = BottomSheetListAdapter(actionList, context)
    }

    interface BottomSheetItemListener{
        fun selectedItem(value: String)
    }

    fun setTitle(text: String){
        if(tvTitle!=null){
            tvTitle.setText(text)
        }
    }
}

