package com.example.clockapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockapp.db.repositories.ClockRepository
import com.example.clockapp.db.entities.Alarm
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ClockViewModel(
    private val repository: ClockRepository
): ViewModel() {

    fun insert(alarm: Alarm) = viewModelScope.launch {
        repository.insert(alarm)
    }

    fun delete(alarm: Alarm) = viewModelScope.launch {
        repository.delete(alarm)
    }

    fun getAllAlarms() = repository.getAllAlarms()

    fun getAlarmByIdAndDeactivateSwitch(alarmID: Int){
        viewModelScope.launch {
            val alarm = repository.getAlarmByID(alarmID)
            alarm.isAlarmEnabled = false
            insert(alarm)
        }
    }
}