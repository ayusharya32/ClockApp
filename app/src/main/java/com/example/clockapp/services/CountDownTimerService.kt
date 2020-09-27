package com.example.clockapp.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.clockapp.util.Utilities.ACTION_PAUSE_TIMER
import com.example.clockapp.util.Utilities.ACTION_RESET_TIMER
import com.example.clockapp.util.Utilities.ACTION_START_OR_RESUME_TIMER
import com.example.clockapp.util.Utilities.TOTAL_TIMER_TIME
import kotlinx.coroutines.*

class CountDownTimerService: LifecycleService() {

    var totalTimerTime = 0L
    private var job: Job? = null

    companion object{
        var isFirstRun = true
        var isTimerRunning = MutableLiveData<Boolean>()
        var timeLeftInMillis = MutableLiveData<Long>()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_TIMER -> {
                    if(isFirstRun){
                        totalTimerTime =  it.getLongExtra(TOTAL_TIMER_TIME, 10000)
                        isFirstRun = false
                        startTimer()
                    }else{
                        startTimer()
                    }
                }
                ACTION_PAUSE_TIMER -> {
                    pauseTimer()
                }
                ACTION_RESET_TIMER -> {
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var lapTime = 0L
    private var timeLeft = 0L
    private var timeStarted = 0L

    private fun startTimer() {
        isTimerRunning.postValue(true)
        timeStarted = System.currentTimeMillis()
        timeLeft = totalTimerTime

        job = CoroutineScope(Dispatchers.Main).launch {
            while(isTimerRunning.value!!){
                if(timeLeft > 0L){
                    lapTime = System.currentTimeMillis() - timeStarted
                    timeLeftInMillis.postValue(totalTimerTime - lapTime)
                    timeLeft = totalTimerTime - lapTime

                    delay(1000)
                } else {
                    killService()
                }
            }
            totalTimerTime -= lapTime
        }
    }

    private fun pauseTimer() {
        isTimerRunning.postValue(false)
    }

    private fun postInitialValues() {
        isTimerRunning.value = false
        timeLeftInMillis.value = 0L
        isFirstRun = true
    }

    private fun killService() {
        postInitialValues()
        job?.cancel()
        stopSelf()
    }
}