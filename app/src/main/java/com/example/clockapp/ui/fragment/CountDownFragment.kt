package com.example.clockapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.clockapp.R
import com.example.clockapp.services.CountDownTimerService
import com.example.clockapp.util.Utilities
import kotlinx.android.synthetic.main.fragment_countdown.*
import kotlinx.android.synthetic.main.fragment_countdown.btnReset
import kotlinx.android.synthetic.main.fragment_countdown.btnStart
import java.util.concurrent.TimeUnit

class CountDownFragment: Fragment(R.layout.fragment_countdown) {

    private var isCountDownTimerRunning  = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isCountDownTimerRunning){
            btnStart.text = "Pause"
            disableNumberPickers()
            btnReset.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }
        setUpNumberPickers()

        subscribeToObservers()

        btnStart.setOnClickListener {
            toggleButtons()
            disableNumberPickers()
            btnReset.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }

        btnReset.setOnClickListener {
            sendCommandToService(Utilities.ACTION_RESET_TIMER)
            resetTimer()
        }

    }

    private fun subscribeToObservers() {
        CountDownTimerService.isTimerRunning.observe(viewLifecycleOwner, Observer {
            isCountDownTimerRunning = it
        })

        CountDownTimerService.timeLeftInMillis.observe(viewLifecycleOwner, Observer {
            val formattedTime = Utilities.getFormattedStopWatchTime(it, false)
            tvTimer.text = formattedTime

            if(formattedTime == "00:00:00"){
                resetTimer()
            }
        })
    }

    private fun resetTimer() {
        enableNumberPickers()
        isCountDownTimerRunning = false
        btnStart.text = "Start"
        btnReset.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
    }

    private fun toggleButtons() {
        if(!isCountDownTimerRunning){
            Log.d("County","IN PAUSE")
            btnStart.text = "Pause"
            sendCommandToService(Utilities.ACTION_START_OR_RESUME_TIMER)
        } else {
            Log.d("County","IN RESUME")
            btnStart.text = "Resume"
            sendCommandToService(Utilities.ACTION_PAUSE_TIMER)
        }
    }

    private fun sendCommandToService(action: String) {
        val intent = Intent(requireContext(), CountDownTimerService::class.java).apply {
            this.action = action
        }
        if(CountDownTimerService.isFirstRun){
            intent.putExtra(Utilities.TOTAL_TIMER_TIME, getValueInMillisFromNumberPickers())
        }
        requireContext().startService(intent)
    }

    private fun getValueInMillisFromNumberPickers() : Long{
        val hoursInMillis = TimeUnit.HOURS.toMillis(npHour.value.toLong())
        val minInMillis = TimeUnit.MINUTES.toMillis(npMinute.value.toLong())
        val secInMillis = TimeUnit.SECONDS.toMillis(npSecond.value.toLong())

        return hoursInMillis + minInMillis + secInMillis
    }

    private fun disableNumberPickers() {
        npHour.isEnabled = false
        npMinute.isEnabled = false
        npSecond.isEnabled = false
    }

    private fun enableNumberPickers() {
        npHour.isEnabled = true
        npMinute.isEnabled = true
        npSecond.isEnabled = true
    }

    private fun setUpNumberPickers() {
        npHour.apply {
            maxValue = 23
            wrapSelectorWheel = true
            minValue = 0
            setFormatter(){
                return@setFormatter String.format("%02d", it)
            }
        }

        npMinute.apply {
            maxValue = 59
            wrapSelectorWheel = true
            minValue = 0
            value = 10
            setFormatter(){
                return@setFormatter String.format("%02d", it)
            }
        }

        npSecond.apply {
            maxValue = 59
            wrapSelectorWheel = true
            minValue = 0
            setFormatter(){
                return@setFormatter String.format("%02d", it)
            }
        }
    }
}