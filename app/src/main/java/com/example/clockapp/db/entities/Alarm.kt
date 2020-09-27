package com.example.clockapp.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alarm_table")
data class Alarm(
    var alarmName: String? = null,
    var hourOfDay: Int? = null,
    var minute: Int? = null,
    var isAlarmEnabled: Boolean? = false,
    var isVibrateEnabled: Boolean? = false
): Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
