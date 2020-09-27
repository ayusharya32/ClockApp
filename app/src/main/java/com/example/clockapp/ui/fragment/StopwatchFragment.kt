package com.example.clockapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.clockapp.R
import com.example.clockapp.services.StopwatchService
import com.example.clockapp.util.Utilities
import kotlinx.android.synthetic.main.fragment_stopwatch.*

class StopwatchFragment: Fragment(R.layout.fragment_stopwatch) {

    private var isStopWatchEnabled = false
    private var timeRunInMillis = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isStopWatchEnabled){
            btnStart.text = "Pause"
            btnReset.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }

        btnStart.setOnClickListener {
            toggleStopWatch()
            btnReset.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        }

        btnReset.setOnClickListener {
            sendCommandToService(Utilities.ACTION_RESET_STOPWATCH)
            resetStopWatch()
        }

        subscribeToObservers()

    }

    private fun resetStopWatch() {
        btnStart.text = "Start"
        btnReset.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
        tvTimer.text = "00:00:00:00"
    }

    private fun sendCommandToService(action: String) {
        val intent = Intent(requireContext(), StopwatchService::class.java).apply {
            this.action = action
        }
        requireContext().startService(intent)
    }

    private fun toggleStopWatch() {
        if(!isStopWatchEnabled){
            sendCommandToService(Utilities.ACTION_START_OR_RESUME_STOPWATCH)
            btnStart.text = "Pause"
        } else {
            sendCommandToService(Utilities.ACTION_PAUSE_STOPWATCH)
            btnStart.text = "Resume"
        }
    }

    private fun subscribeToObservers() {
        StopwatchService.isStopWatchRunning.observe(viewLifecycleOwner, Observer {
            isStopWatchEnabled = it
        })

        StopwatchService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            timeRunInMillis = it
            val formattedTime = Utilities.getFormattedStopWatchTime(timeRunInMillis)
            tvTimer.text = formattedTime
        })
    }
}