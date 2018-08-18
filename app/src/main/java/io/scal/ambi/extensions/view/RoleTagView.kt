package io.scal.ambi.extensions.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.ambi.work.R
import io.scal.ambi.entity.user.UserType


/**
 * Created by chandra on 09-08-2018.
 */

class RoleTagView: LinearLayout {
    var roleCommaSeperated: String = ""

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialise(attrs,0,context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialise(attrs,defStyleAttr,context)
    }

    private fun initialise(attrs: AttributeSet, defStyleAttr: Int, context: Context){
        val ta = context.theme .obtainStyledAttributes(attrs, R.styleable.RoleTagView, defStyleAttr, 0)
        try
        {
            roleCommaSeperated = ta.getString(R.styleable.RoleTagView_rtv_roleCommaSeperated)

            if(!roleCommaSeperated.isNullOrEmpty()){

                var listToPlot = roleCommaSeperated.split(",")
                for(s:String in listToPlot){
                    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    var view = inflater.inflate(R.layout.role_item, this) as TextView
                    view.setText(s.toLowerCase())
                    view.setBackgroundResource(UserType.valueOf(s.toUpperCase()).colorId)
                }
            }
        }
        finally
        {
            ta.recycle()
        }
    }
}