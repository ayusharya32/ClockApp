package com.example.clockapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.clockapp.R
import com.example.clockapp.db.entities.Alarm
import com.example.clockapp.ui.activity.MainActivity
import com.example.clockapp.ui.viewmodel.ClockViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.app_bar_layout.*
import kotlinx.android.synthetic.main.fragment_set_alarm.*

class SetAlarmFragment: Fragment(R.layout.fragment_set_alarm) {

    private lateinit var viewModel: ClockViewModel
    private val args: SetAlarmFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        if(args.alarm != null){
            setSavedInitialValues(args.alarm!!)
        }

        btnSave.setOnClickListener {
            viewModel.insert(getCurrentAlarm())
            findNavController().navigate(R.id.action_setAlarmFragment2_to_viewPagerFragment)

            Snackbar.make(requireView(), "Alarm Added Successfully", Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun getCurrentAlarm(): Alarm{
        val alarmName = etAlarmName.text.toString()
        val hourOfDay = tpClock.hour
        val minute = tpClock.minute
        val isAlarmEnabled = false
        val isVibrateEnabled = smVibrate.isChecked

        val alarm = Alarm(alarmName, hourOfDay, minute, isAlarmEnabled, isVibrateEnabled)
        if(args.alarm != null){
            alarm.id = args.alarm!!.id
        }
        return alarm
    }

    private fun setSavedInitialValues(alarm: Alarm){
        tpClock.hour = alarm.hourOfDay!!
        tpClock.minute = alarm.minute!!
        etAlarmName.setText(alarm.alarmName)
        smVibrate.isChecked = alarm.isVibrateEnabled!!
    }
}