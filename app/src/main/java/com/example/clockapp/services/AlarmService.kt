package com.example.clockapp.services

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.example.clockapp.BaseApplication
import com.example.clockapp.R
import com.example.clockapp.ui.activity.MainActivity
import com.example.clockapp.util.Utilities
import com.example.clockapp.util.Utilities.ACTION_START_ALARM
import com.example.clockapp.util.Utilities.ACTION_STOP_ALARM
import com.example.clockapp.util.Utilities.NOTIFICATION_ID
import kotlinx.coroutines.*


class AlarmService: LifecycleService(){

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var ringtoneJob: Job
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var vibrator: Vibrator
    private lateinit var alarmTone: Ringtone
    private var alarmID = 0
    private var alarmName = ""
    private var isVibrateEnabled = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getInitialValues(intent)

        intent?.let {
            when(it.action){
                ACTION_START_ALARM -> {
                    ringAlarm()
                    createNotification()
                    saveIdsInSharedPrefs()

                    if(isVibrateEnabled) {
                        startVibration()
                    }
                }

                ACTION_STOP_ALARM -> {
                    killServiceAndCancelNotification()
                }

            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun getInitialValues(intent: Intent?) {
        sharedPrefs = this.getSharedPreferences(
            Utilities.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

        if(intent?.extras != null){
            alarmID = intent.getIntExtra(Utilities.ALARM_ID_KEY, 0)
            alarmName = intent.getStringExtra(Utilities.ALARM_NAME_KEY)!!
            isVibrateEnabled = intent.getBooleanExtra(Utilities.ALARM_VIBRATE_KEY, false)
        }
    }

    private fun saveIdsInSharedPrefs() {
        val idString = sharedPrefs.getString(Utilities.ID_STRING, "") + "$alarmID,"
        sharedPrefs.edit()
            .putString(Utilities.ID_STRING, idString)
            .apply()
    }

    private fun ringAlarm(){
        var alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if(alarmToneUri == null){
            alarmToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        }
        alarmTone = RingtoneManager.getRingtone(this, alarmToneUri)

        ringtoneJob = CoroutineScope(Dispatchers.Main).launch {
            withTimeout(60000L){
                repeat(59){
                    if(!alarmTone.isPlaying){
                        alarmTone.play()
                    }
                    delay(1000L)
                }
                killService()
            }
        }
    }

    private fun startVibration(){
        vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val longArray = longArrayOf(0, 1000, 1000, 1000, 1000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(longArray, 0))
        } else {
            //deprecated in API 26
            vibrator.vibrate(longArray, 0)
        }

    }

    private fun createNotification(){
        val currentTime = Utilities.getCurrentTime()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val cancelIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val cancelPendingIntent = PendingIntent.getService(this, 0, cancelIntent,
            FLAG_UPDATE_CURRENT)

        notificationManager = NotificationManagerCompat.from(this)
        val notification = NotificationCompat.Builder(this, BaseApplication.CHANNEL_ID_1)
            .setContentTitle(currentTime)
            .setContentText(if(alarmName.isEmpty()) "It's time to wake up!" else alarmName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSmallIcon(R.drawable.ic_clock)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_clock, "Cancel", cancelPendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)

    }

    private fun killService() {
        if(isVibrateEnabled) {
            vibrator.cancel()
        }
        ringtoneJob.cancel()
        alarmTone.stop()
        stopSelf()
    }

    private fun killServiceAndCancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
        killService()
    }

}