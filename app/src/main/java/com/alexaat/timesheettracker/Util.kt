package com.alexaat.timesheettracker


import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.alexaat.timesheettracker.database.WorkDay
import java.text.SimpleDateFormat
import java.util.*

fun Fragment.hideKeyboard() {
    val activity = this.activity
    if (activity is AppCompatActivity) {
        activity.hideKeyboard()
    }
}
fun AppCompatActivity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

}


fun format(time:Long):String{
    val pattern = "dd-MMM-yyyy HH:mm:ss"
    val sdf = SimpleDateFormat(pattern, Locale.UK)
    return sdf.format(time)
}

fun formatWorkDays(workDays:List<WorkDay>, resources:Resources): Spanned {
   val sb = StringBuilder()
   sb.apply {
       workDays.forEach {
            append("<b>${resources.getString(R.string.time_in)}</b>&nbsp;")
            append("${format(it.clockIn)}<br>")
            append("<b>${resources.getString(R.string.time_out)}</b>&nbsp;")
            if(it.clockIn!=it.clockOut){
                append("${format(it.clockOut)}")
            }
           append("<br><b>${resources.getString(R.string.time_at_work)}</b>&nbsp;")
           if(it.clockIn!=it.clockOut){
               val elapsedTime =(it.clockOut-it.clockIn)/1000
               append("${DateUtils.formatElapsedTime(elapsedTime)}")
           }
           append("<br><b>${resources.getString(R.string.description)}</b>&nbsp;")
           append("${it.description}<br><br>")
       }
   }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

}

