package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.ShiftType

class Converters {
    @TypeConverter
    fun fromShiftType(value: ShiftType?): String {
        return value?.name ?: ShiftType.MORNING.name
    }

    @TypeConverter
    fun toShiftType(value: String?): ShiftType {
        return try {
            if (value != null) ShiftType.valueOf(value) else ShiftType.MORNING
        } catch (e: Exception) {
            ShiftType.MORNING
        }
    }
}
