package com.example.clockapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.clockapp.db.entities.Alarm

@Dao
interface ClockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarm_table ORDER BY hourOfDay and minute")
    fun getAllAlarm(): LiveData<List<Alarm>>

    @Query("SELECT * FROM alarm_table WHERE id = :alarmID ")
    suspend fun getElementById(alarmID: Int): Alarm
}