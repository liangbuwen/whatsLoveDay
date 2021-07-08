package com.love.whatsloveday

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi

class WhatsToday : AppWidgetProvider() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent != null) {
            if (intent.action=="btn.text.com"){
                if (context != null) {
                    Toast.makeText(context,"已刷新", Toast.LENGTH_SHORT).show()
                    updateAppWidget(context, AppWidgetManager.getInstance(context),R.layout.whats_today)
                }
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.whats_today)

    views.setTextViewText(R.id.appwidget_text, getNumbersOfToday())

    // Instruct the widget manager to update the widget
    views.setOnClickPendingIntent(R.id.appwidget_text, getPendingIntent(context,R.id.appwidget_text))
    appWidgetManager.updateAppWidget(appWidgetId, views)

}

@RequiresApi(Build.VERSION_CODES.N)
fun getNumbersOfToday(): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val moth = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val nows = SimpleDateFormat("yyyy-MM-dd")
    val diff = nows.parse("$year-${moth + 1}-$day").time - nows.parse("2019-08-15").time

    val howDay: Long
    val nd = (1000 * 24 * 60 * 60).toLong()// 一天的毫秒数
    val nh = (1000 * 60 * 60).toLong()// 一小时的毫秒数
    val nm = (1000 * 60).toLong()// 一分钟的毫秒数
    val ns: Long = 1000// 一秒钟的毫秒数

    howDay = diff / nd
    val hour = diff % nd / nh
    val min = diff % nd % nh / nm
    val sec = diff % nd % nh % nm / ns

    if (day >= 1) {
        return "${howDay + 1}"
    } else {
        if (day == 0) {
            return "1"
        } else {
            return "0"
        }

    }
}

private fun getPendingIntent(context: Context, resID: Int): PendingIntent {
    val intent = Intent()
    intent.action = "btn.text.com"
    //设置data域的时候，把控件id一起设置进去，
    // 因为在绑定的时候，是将同一个id绑定在一起的，所以哪个控件点击，发送的intent中data中的id就是哪个控件的id
    intent.setClass(context,WhatsToday::class.java)
    intent.data = Uri.parse("id:$resID")

    return PendingIntent.getBroadcast(context, 0, intent, 0)
}
