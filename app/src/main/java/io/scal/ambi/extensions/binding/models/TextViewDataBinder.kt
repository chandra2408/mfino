package io.scal.ambi.extensions.binding.models

import android.content.Context
import android.databinding.BindingAdapter
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RotateDrawable
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.ambi.work.R
import io.scal.ambi.entity.feed.PollEndsTime
import io.scal.ambi.entity.user.UserType
import io.scal.ambi.extensions.SystemUtils
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.Seconds
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.PeriodFormatterBuilder
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object TextViewDataBinder {

    private val fontNameTypefaceCache: MutableMap<String, Typeface> = HashMap()
    private val fontNameWithResource = mapOf(
            Pair("pantraRegular", R.font.nicolas_desle_pantra_regular),
            Pair("pantraBold", R.font.nicolas_desle_pantra_bold),
            Pair("pantraLight", R.font.nicolas_desle_pantra_light),
            Pair("pantraMedium", R.font.nicolas_desle_pantra_medium)
    )

    @JvmStatic
    @BindingAdapter("customFontName")
    fun bindCustomFont(textView: TextView, fontName: String?) {
        fontName?.run {
            fontNameTypefaceCache[fontName]?.run {
                textView.typeface = this
                return
            }

            val fontResource = fontNameWithResource[this]
            fontResource?.run {
                val typeface = ResourcesCompat.getFont(textView.context, this)
                if (null != typeface) {
                    textView.typeface = typeface
                    fontNameTypefaceCache.put(fontName, typeface)
                }
            }
        }
    }

    private val FULL_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy/MM/dd").withLocale(Locale.ENGLISH)
    private val TIME_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mma").withLocale(Locale.ENGLISH)
    private val DAY_OF_WEEK_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEEE").withLocale(Locale.ENGLISH)
    private val DATE_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("MMM dd").withLocale(Locale.ENGLISH)

    @JvmStatic
    @BindingAdapter("datePassed")
    fun bindDateTimePassed(textView: TextView, dateTime: DateTime?) {
        if (null == dateTime) {
            textView.text = null
        } else {
            // we can use build in solution or a library here, but I don't know restrictions now, so will create simple code for that
            val context = textView.context
            val nowDateTime = DateTime.now()
            val durationBetween = Seconds.secondsBetween(dateTime, nowDateTime).toStandardDuration()
            val resultText = when {
                durationBetween.standardMinutes < -20 -> FULL_DATE_TIME_FORMATTER.print(dateTime)
                durationBetween.standardSeconds < 60  -> context.getString(R.string.day_ago_just_now)
                durationBetween.standardMinutes in 2..59 -> context.getString(R.string.day_ago_minutes_ago, durationBetween.standardMinutes)
                durationBetween.standardMinutes in 1..1 -> context.getString(R.string.day_ago_minute_ago, durationBetween.standardMinutes)
                durationBetween.standardHours < 24    -> context.getString(R.string.day_ago_hours_ago, durationBetween.standardHours)
                durationBetween.standardDays < 30     -> context.getString(R.string.day_ago_days_ago, durationBetween.standardDays)
                else                                  -> FULL_DATE_TIME_FORMATTER.print(dateTime)
            }
            textView.text = resultText
        }
    }

    @JvmStatic
    @BindingAdapter("roleValues")
    fun bindRoleTag(linearLayout: LinearLayout,roleCommaSeperated: String) {
        if(!roleCommaSeperated.isNullOrEmpty()){
            var listToPlot = roleCommaSeperated.split(",").distinct()
            linearLayout.removeAllViews()
            for(s:String in listToPlot){
                if(s.isNotEmpty()){
                    var context = linearLayout.context
                    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    var view = inflater.inflate(R.layout.role_item, null)
                    var textView = view.findViewById<TextView>(R.id.tv_actor_category) as TextView
                    textView.text = s.trim().toLowerCase()
                    textView.setBackgroundResource(UserType.valueOf(s.toUpperCase()).colorId)
                    linearLayout.addView(view)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("otherText","textToBold", requireAll = true)
    fun partlyBold(textView: TextView, actualText: String, textToBold: String) {
        val builder = SpannableStringBuilder()

        if (textToBold.length > 0 && textToBold.trim { it <= ' ' } != "") {
            //for counting start/end indexes
            val testText = actualText.toLowerCase(Locale.US)
            val testTextToBold = textToBold.toLowerCase(Locale.US)
            val startingIndex = testText.indexOf(testTextToBold)
            val endingIndex = startingIndex + testTextToBold.length
            //for counting start/end indexes

            if (startingIndex < 0 || endingIndex < 0) {
                textView.text = builder.append(actualText)
            } else if (startingIndex >= 0 && endingIndex >= 0) {
                builder.append(actualText)
                builder.setSpan(StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0)
                textView.text = builder
            }
        } else {
            textView.text = builder.append(actualText)
        }
    }



    @JvmStatic
    @BindingAdapter("datePassedSmall")
    fun bindDateTimePassedSmall(textView: TextView, dateTime: DateTime?) {
        if (null == dateTime) {
            textView.text = null
        } else {
            // we can use build in solution or a library here, but I don't know restrictions now, so will create simple code for that
            val context = textView.context
            val nowDateTime = DateTime.now()
            val durationBetween = Seconds.secondsBetween(dateTime, nowDateTime).toStandardDuration()
            val resultText = when {
                durationBetween.standardMinutes < -20 -> DATE_DATE_TIME_FORMATTER.print(dateTime)
                durationBetween.standardSeconds < 60  -> context.getString(R.string.day_ago_just_now_small)
                durationBetween.standardMinutes < 60  -> context.getString(R.string.day_ago_minutes_ago, durationBetween.standardMinutes)
                durationBetween.standardHours < 24    -> context.getString(R.string.day_ago_hours_ago, durationBetween.standardHours)
                durationBetween.standardDays < 7      -> DAY_OF_WEEK_DATE_TIME_FORMATTER.print(dateTime)
                else                                  -> DATE_DATE_TIME_FORMATTER.print(dateTime)
            }
            textView.text = resultText
        }
    }

    @JvmStatic
    @BindingAdapter("durationPollEnds")
    fun bindDurationPollEnds(textView: TextView, pollEndsTime: PollEndsTime?) {
        if (null == pollEndsTime) {
            textView.text = null
        } else {
            val context = textView.context
            val formatter = PeriodFormatterBuilder()
                    .appendDays()
                    .appendSuffix("d")
                    .appendHours()
                    .appendSuffix("h")
                    .appendMinutes()
                    .appendSuffix("m")
                    .toFormatter()

            val text =
                    when (pollEndsTime) {
                        is PollEndsTime.OneDay       -> context.getString(R.string.creation_poll_ends_duration_1_day)
                        is PollEndsTime.OneWeek      -> context.getString(R.string.creation_poll_ends_duration_1_week)
                        is PollEndsTime.UserCustom   -> context.getString(R.string.creation_poll_ends_duration_custom_title)
                        PollEndsTime.Never           -> context.getString(R.string.creation_poll_ends_duration_never)
                        is PollEndsTime.TimeDuration -> throw IllegalStateException("todo implement custom text")
                    }
            textView.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("durationPollEndsIn")
    fun bindDurationPollEndsIn(textView: TextView, pollEndsTime: DateTime?) {
        if (null == pollEndsTime) {
            textView.text = null
        } else {
            val context = textView.context
            val formatter = PeriodFormatterBuilder()
                    .appendDays()
                    .appendSuffix("d ")
                    .appendHours()
                    .appendSuffix("h ")
                    .appendMinutes()
                    .appendSuffix("m")
                    .toFormatter()

            val pollDurationLeft = Duration(DateTime.now(), pollEndsTime)
            if (pollDurationLeft.millis < 0) {
                // poll was ended
                textView.text = null
            } else {
                textView.text = context.getString(R.string.news_feed_poll_ends_in, formatter.print(pollDurationLeft.toPeriodFrom(DateTime.now())))
            }
        }
    }

    @JvmStatic
    @BindingAdapter("inputType")
    fun bindInputType(editText: EditText, inputType: Int?) {
        if (null == inputType || -1 == inputType) {
        } else {
            editText.inputType = inputType

            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.isCursorVisible = true
        }
    }

    @JvmStatic
    @BindingAdapter("textResId")
    fun bindTextRes(textView: TextView, textRes: Int?) {
        if (textRes == null || textRes == 0) {
            textView.text = null
        } else {
            textView.setText(textRes)
        }
    }

    @JvmStatic
    @BindingAdapter("boldTextStyle")
    fun bindBoldTextStyle(textView: TextView, boldText: Boolean?) {
        if (null != boldText && boldText) {
            textView.setTypeface(null, Typeface.BOLD)
        } else {
            textView.setTypeface(null, Typeface.NORMAL)
        }
    }

    @JvmStatic
    @BindingAdapter("maxLines")
    fun bindMaxLines(textView: TextView, maxLines: Int?) {
        if (null == maxLines || 0 >= maxLines) {
            textView.maxLines = -1
        } else {
            textView.maxLines = maxLines
        }
    }

    @JvmStatic
    @BindingAdapter("imeDoneClickListener")
    fun bindImeDoneClickListener(editText: EditText, imeDoneClickListener: DoneActionClickListener?) {
        if (null == imeDoneClickListener) {
            editText.setOnEditorActionListener(null)
        } else {
            val listener = TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.action == KeyEvent.ACTION_DOWN
                        && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    imeDoneClickListener.onDoneActionClicked()
                    true
                } else {
                    false
                }
            }
            editText.setOnEditorActionListener(listener)
        }
    }

    @JvmStatic
    @BindingAdapter("notebookBackground")
    fun bindNotebookColor(textView: View, color: String?) = try{
        if(textView.background is LayerDrawable){
            var layers = textView.background as LayerDrawable
            var rotate = layers.findDrawableByLayerId( R.id.shape_id ) as RotateDrawable
            var drawable = rotate.drawable as GradientDrawable
            drawable.setColor(SystemUtils.parseColor(color))
        }else{
            (textView.getBackground() as GradientDrawable).setColor(SystemUtils.parseColor(color))
        }
    }catch (e: Exception){
        Timber.e("bindNotebookColor=",e.message)
        if(textView.background is LayerDrawable){
            //do nothing
        }else{
            (textView.getBackground() as GradientDrawable).setColor(ContextCompat.getColor(textView.context,R.color.blue))
        }
    }

    @JvmStatic
    @BindingAdapter("viewBackgroundColor")
    fun bindNotebookBackgroundColor(textView: View, color: String?) = try{
        textView.setBackgroundColor(Color.parseColor(color))
    }catch (e: Exception){
        Timber.e("bindNotebookBackgroundColor=",e.message)
        textView.setBackgroundColor(ContextCompat.getColor(textView.context,R.color.blue))
    }


    @JvmStatic
    @BindingAdapter("btnStatePressed")
    fun bindButtonState(editText: Button,state: Boolean) {
        editText.isSelected = state
        editText.isPressed = state
    }

    @JvmStatic
    @BindingAdapter("notebookUpdatedAt")
    fun notebookUpdatedAtFormatter(textview: TextView, time: String?){
        if(time.isNullOrEmpty()){
            textview.setText("Unknown")
        }else{
            val formatter = SimpleDateFormat("MMM dd hh:mma", Locale.US)
            val dateString = formatter.format(Date(time!!.toLong()))
            var data = dateString.split(" ")
            if(data.size == 3){
                textview.setText("Edited " + data.get(0)+" " + data.get(1) + " At " + data.get(2))
            }else{
                textview.setText(dateString)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("notebookUploadedAt")
    fun notebookUploadedAtFormatter(textview: TextView, time: String?){
        if(time.isNullOrEmpty()){
            textview.setText("Unknown")
        }else{
            val format: SimpleDateFormat
            if (time!!.endsWith("Z")) {
                format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                format.timeZone = TimeZone.getTimeZone("GMT")
            } else {
                format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            }
            try {
                val formatter2 = SimpleDateFormat("MMM dd hh:mma", Locale.US)
                val dateString = formatter2.format(format.parse(time))
                var data = dateString.split(" ")
                if(data.size == 3){
                    textview.setText("Uploaded " + data.get(0)+" " + data.get(1) + " At " + data.get(2))
                }else{
                    textview.setText(dateString)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                Log.e("Problem with format", time)
            }
        }
    }


    @JvmStatic
    @BindingAdapter("webURL")
    fun notebookUploadedAtFormatter(textview: WebView, url: String?){
        textview.getSettings().setJavaScriptEnabled(true);
        textview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }

        textview.loadUrl(url);
    }

    @JvmStatic
    @BindingAdapter("textChangedListener")
    fun bindTextChangedListener(editText: EditText, textWatcher: TextWatcher?) {
        if (null!=textWatcher) {
            editText.addTextChangedListener(textWatcher);
        }
    }

    interface DoneActionClickListener {
        fun onDoneActionClicked()
    }
}