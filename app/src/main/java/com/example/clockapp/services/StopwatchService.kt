package com.example.clockapp.services

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.clockapp.util.Utilities.ACTION_PAUSE_STOPWATCH
import com.example.clockapp.util.Utilities.ACTION_RESET_STOPWATCH
import com.example.clockapp.util.Utilities.ACTION_START_OR_RESUME_STOPWATCH
import com.example.clockapp.util.Utilities.TIMER_UPDATE_INTERVAL
import kotlinx.coroutines.*

class StopwatchService: LifecycleService() {

    private var job: Job? = null

    companion object{
        var isFirstRun = true
        var isStopWatchRunning = MutableLiveData<Boolean>()
        var timeRunInMillis = MutableLiveData<Long>()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_STOPWATCH -> {
                    isFirstRun = false
                    startStopWatch()
                }
                ACTION_PAUSE_STOPWATCH -> {
                    pauseStopWatch()
                }
                ACTION_RESET_STOPWATCH -> {
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var timeRun = 0L
    private var lapTime = 0L
    private var timeStarted = 0L

    private fun startStopWatch() {
        isStopWatchRunning.value = true
        timeStarted = System.currentTimeMillis()

        job = CoroutineScope(Dispatchers.Main).launch {
            while(isStopWatchRunning.value!!){
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(lapTime + timeRun)

                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun pauseStopWatch(){
        isStopWatchRunning.postValue(false)
    }

    private fun postInitialValues() {
        isStopWatchRunning.value = false
        timeRunInMillis.value = 0L
        isFirstRun = true
    }

    private fun killService() {
        job?.cancel()
        postInitialValues()
        stopSelf()
    }

}