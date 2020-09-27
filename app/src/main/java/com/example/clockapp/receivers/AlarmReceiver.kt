package com.example.clockapp.receivers

import android.app.Notification
import android.app.PendingIntent
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.content.Context.POWER_SERVICE
import android.hardware.display.DisplayManager
import android.icu.util.ULocale
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Display
import android.view.Window
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.clockapp.BaseApplication.Companion.CHANNEL_ID_1
import com.example.clockapp.R
import com.example.clockapp.db.ClockDatabase
import com.example.clockapp.db.entities.Alarm
import com.example.clockapp.db.repositories.ClockRepository
import com.example.clockapp.services.AlarmService
import com.example.clockapp.ui.activity.MainActivity
import com.example.clockapp.ui.viewmodel.ClockViewModel
import com.example.clockapp.ui.viewmodel.ClockViewModelFactory
import com.example.clockapp.util.Utilities
import com.example.clockapp.util.Utilities.ACTION_START_ALARM
import com.example.clockapp.util.Utilities.ALARM_ID_KEY
import com.example.clockapp.util.Utilities.ALARM_NAME_KEY
import com.example.clockapp.util.Utilities.ALARM_VIBRATE_KEY
import com.example.clockapp.util.Utilities.ID_STRING
import com.example.clockapp.util.Utilities.NOTIFICATION_ID
import com.example.clockapp.util.Utilities.SHARED_PREFERENCES_NAME
import com.example.clockapp.util.Utilities.getCurrentTime
import kotlinx.coroutines.*
import java.util.*

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent?.extras != null){
            val alarmID = intent.getIntExtra(ALARM_ID_KEY, 0)
            val alarmName = intent.getStringExtra(ALARM_NAME_KEY)!!
            val isVibrateEnabled = intent.getBooleanExtra(ALARM_VIBRATE_KEY, false)

            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_START_ALARM
                putExtra(ALARM_ID_KEY, alarmID)
                putExtra(ALARM_NAME_KEY, alarmName)
                putExtra(ALARM_VIBRATE_KEY, isVibrateEnabled)
            }

            Log.d("Intenty","Inside Receiver")
            context?.startService(intent)
        }

    }


}