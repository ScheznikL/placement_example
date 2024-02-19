package com.endofjanuary.placement_example.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import io.github.sceneview.model.ModelInstance

class Converters {
    //    @TypeConverter fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis
    //    @TypeConverter fun datestampToCalendar(value: Long): Calendar =
    //        Calendar.getInstance().apply { timeInMillis = value }
    @TypeConverter
    fun modelInstanceToString(modelInstance: ModelInstance): String = Gson().toJson(modelInstance)
    @TypeConverter
    fun stringToModelInstance(jsonString: String): ModelInstance =
        Gson().fromJson(jsonString, ModelInstance::class.java)

}