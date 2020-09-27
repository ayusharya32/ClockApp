package com.example.clockapp.ui.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clockapp.R
import com.example.clockapp.adapter.AlarmRecyclerViewAdapter
import com.example.clockapp.db.entities.Alarm
import com.example.clockapp.receivers.AlarmReceiver
import com.example.clockapp.ui.activity.MainActivity
import com.example.clockapp.ui.viewmodel.ClockViewModel
import com.example.clockapp.util.Utilities
import com.example.clockapp.util.Utilities.ID_STRING
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_alarm.*
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmFragment: Fragment(R.layout.fragment_alarm) {

    private lateinit var viewModel: ClockViewModel
    private lateinit var alarmAdapter: AlarmRecyclerViewAdapter
    private lateinit var alarmManager: AlarmManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        updateSwitches()

        setUpRecyclerView()
        setRecyclerViewListeners()
        setSwipeToDelete()

        fabAlarm.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("alarm", null)
            }
            findNavController().navigate(R.id.action_viewPagerFragment_to_setAlarmFragment2, bundle)
        }

        viewModel.getAllAlarms().observe(viewLifecycleOwner, Observer {
            alarmAdapter.differ.submitList(it)
        })
    }

    private fun setUpRecyclerView() {
        alarmAdapter = AlarmRecyclerViewAdapter()
        rvAlarm.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = alarmAdapter
        }
    }

    private fun setRecyclerViewListeners() {
        //Switch Check Change Listener

        alarmAdapter.setOnToggleClickListener {currentItem ->
            val state = currentItem.isAlarmEnabled
            currentItem.isAlarmEnabled = !state!!
            viewModel.insert(currentItem)

            if(currentItem.isAlarmEnabled!!){
                setAlarm(currentItem)
            } else {
                cancelAlarm(currentItem)
            }
        }

        //Item Click Listener
        alarmAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("alarm", it)
            }
            findNavController().navigate(R.id.action_viewPagerFragment_to_setAlarmFragment2, bundle)
        }
    }

    private fun setAlarm(alarm: Alarm){
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)
            .putExtra(Utilities.ALARM_ID_KEY, alarm.id)
            .putExtra(Utilities.ALARM_NAME_KEY, alarm.alarmName)
            .putExtra(Utilities.ALARM_VIBRATE_KEY, alarm.isVibrateEnabled)

        val pendingIntent = PendingIntent.getBroadcast(requireContext(), alarm.id!!, alarmIntent, 0)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, alarm.hourOfDay!!)
        calendar.set(Calendar.MINUTE, alarm.minute!!)
        calendar.set(Calendar.SECOND, 0)

        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE, 1)
        }

        val timeDifferenceInMillis = calendar.timeInMillis - Calendar.getInstance().timeInMillis

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(
            requireContext(),
            getFormattedTimeDifference(timeDifferenceInMillis),
            Toast.LENGTH_SHORT).show()

    }

    private fun cancelAlarm(alarm: Alarm){
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)
            .putExtra(Utilities.ALARM_ID_KEY, alarm.id)
            .putExtra(Utilities.ALARM_NAME_KEY, alarm.alarmName)
            .putExtra(Utilities.ALARM_VIBRATE_KEY, alarm.isVibrateEnabled)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), alarm.id!!, alarmIntent, 0)

        alarmManager.cancel(pendingIntent)

        Toast.makeText(requireContext(), "Alarm Cancelled", Toast.LENGTH_SHORT).show()
    }

    private fun getIdsAndClearString(): MutableList<Int>{
        val idList: MutableList<Int> = arrayListOf()
        val sharedPrefs = context?.getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        val idString = sharedPrefs?.getString(ID_STRING,"-1")

        sharedPrefs?.edit()?.remove(ID_STRING)?.apply()

        if("-1" != idString){
            val tempList = idString?.split(",")?.toTypedArray()!!
            for(item in tempList){
                if(item != "") {
                    idList.add(item.toInt())
                }
            }
        }
        return idList
    }

    private fun updateSwitches() {
        val idList = getIdsAndClearString()

        for(id in idList){
            viewModel.getAlarmByIdAndDeactivateSwitch(id)
        }
    }

    private fun setSwipeToDelete() {
        val itemTouchCallBack =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val alarmItem = alarmAdapter.differ.currentList[position]

                    viewModel.delete(alarmItem)
                    if (alarmItem.isAlarmEnabled!!){
                        cancelAlarm(alarmItem)
                    }
                    Snackbar.make(requireView(), "Alarm Deleted Successfully", Snackbar.LENGTH_SHORT).show()
                }
            }

        ItemTouchHelper(itemTouchCallBack).attachToRecyclerView(rvAlarm)
    }

    private fun getFormattedTimeDifference(ms: Long): String {
        var milliSeconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds)
        milliSeconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
        milliSeconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)

        return "Alarm will ring after ${if(hours < 10) "0" else ""}$hours hrs " +
                "${if(minutes < 10) "0" else ""}$minutes min " +
                "${if(seconds < 10) "0" else ""}$seconds sec"
    }

}