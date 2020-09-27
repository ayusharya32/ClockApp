package com.example.clockapp.db.repositories

import com.example.clockapp.db.ClockDatabase
import com.example.clockapp.db.entities.Alarm

class ClockRepository(
    val db: ClockDatabase
){
    suspend fun insert(alarm: Alarm) = db.getClockDao().insertAlarm(alarm)

    suspend fun delete(alarm: Alarm) = db.getClockDao().deleteAlarm(alarm)

    fun getAllAlarms() = db.getClockDao().getAllAlarm()

    suspend fun getAlarmByID(alarmID: Int) = db.getClockDao().getElementById(alarmID)
}