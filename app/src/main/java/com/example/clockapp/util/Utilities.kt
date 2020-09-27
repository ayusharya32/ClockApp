package com.example.clockapp.util


import com.example.clockapp.db.entities.Alarm
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Utilities {
    const val ALARM_ID_KEY = "alarmID"
    const val ALARM_NAME_KEY = "alarmName"
    const val ALARM_VIBRATE_KEY = "alarmVibrate"

    const val SHARED_PREFERENCES_NAME = "sharedPrefs"
    const val ID_STRING = "idString"

    const val NOTIFICATION_ID = 1

    const val ACTION_START_ALARM = "ACTION_START_ALARM"
    const val ACTION_STOP_ALARM = "ACTION_STOP_ALARM"

    const val ACTION_START_OR_RESUME_STOPWATCH = "ACTION_START_OR_RESUME_STOPWATCH"
    const val ACTION_PAUSE_STOPWATCH = "ACTION_PAUSE_STOPWATCH"
    const val ACTION_RESET_STOPWATCH = "ACTION_RESET_STOPWATCH"
    const val TIMER_UPDATE_INTERVAL = 80L

    const val ACTION_START_OR_RESUME_TIMER = "ACTION_START_OR_RESUME_TIMER"
    const val ACTION_PAUSE_TIMER = "ACTION_PAUSE_TIMER"
    const val ACTION_RESET_TIMER = "ACTION_RESET_TIMER"

    const val TOTAL_TIMER_TIME = "TOTAL_TIMER_TIME"

    var isViewPagerFragment = false

    fun getAlarmTime(alarm: Alarm): String{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hourOfDay!!)
        calendar.set(Calendar.MINUTE, alarm.minute!!)
        calendar.set(Calendar.SECOND, 0)

        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.UK)

        return simpleDateFormat.format(calendar.time)
    }

    fun getCurrentTime(): String{
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.UK)

        return simpleDateFormat.format(calendar.time)
    }

    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = true): String {
        var milliSeconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds)
        milliSeconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
        milliSeconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)
        milliSeconds -= TimeUnit.SECONDS.toMillis(seconds)

        if(!includeMillis){
            return "${if(hours < 10) "0" else ""}$hours:" +
                    "${if(minutes < 10) "0" else ""}$minutes:" +
                    "${if(seconds < 10) "0" else ""}$seconds"
        }

        milliSeconds /= 10
        return "${if(hours < 10) "0" else ""}$hours:" +
                "${if(minutes < 10) "0" else ""}$minutes:" +
                "${if(seconds < 10) "0" else ""}$seconds:" +
                "${if(milliSeconds < 10) "0" else ""}$milliSeconds"
    }
}