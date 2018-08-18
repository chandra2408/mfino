package io.scal.ambi.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.WindowManager
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat


/**
 * Created by chandra on 04-08-2018.
 */
object SystemUtils {

    @JvmStatic
    fun toMMMddyyyy(date: DateTime) : String {
        return date.toString(DateTimeFormat.forPattern("MMM dd, yyyy"))
    }

    @JvmStatic
    fun hmmaa(HH: Int, MM: Int) : String {
        val nowDateTime = DateTime.now()
        return ((nowDateTime.hourOfDay().setCopy(HH)).minuteOfHour().setCopy(MM)).toString(DateTimeFormat.forPattern("hh:mm a"))
    }

    @JvmStatic
    fun getDayNameByDayNo(dayNo: Int) : String {
        var day = (when(dayNo){
            1 -> "Mon"
            2 -> "Tue"
            3 -> "Wed"
            4 -> "Thu"
            5 -> "Fri"
            6 -> "Sat"
            else -> "Sun"
        }
                )
        return day;
    }

    @JvmStatic
    fun isImage(url: String): Boolean{
        return (url.endsWith("jpg") || url.endsWith(suffix = "jpeg") || url.endsWith("JPEG") || url.endsWith("JPG") || url.endsWith("png") || url.endsWith("PNG")
                || url.endsWith("svg") || url.endsWith("SVG"))
    }


    fun getViewWidth(view: View): Int {
        val wm = view.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        val deviceWidth: Int

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val size = Point()
            display.getSize(size)
            deviceWidth = size.x
        } else {
            deviceWidth = display.width
        }

        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthMeasureSpec, heightMeasureSpec)
        return view.getMeasuredWidth() //        view.getMeasuredWidth();
    }

    @JvmStatic
    fun okDialog(context: Context, message: String, runnable: Runnable){
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setNeutralButton("OK"){_,_ ->
            runnable.run()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    @JvmStatic
    fun okCancelDialog(context: Context, title: String?, message: String, okRunnable: Runnable, cancelRunnable: Runnable){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Yes"){_,_ ->
            okRunnable.run()
        }
        builder.setNegativeButton("Cancel"){_,_ ->
            cancelRunnable.run()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    @JvmStatic
    fun parseColor(color: String?): Int{
        try{
            return Color.parseColor(color)
        }catch (e: Exception){
            return Color.parseColor("#00A6F0")
        }

    }
}