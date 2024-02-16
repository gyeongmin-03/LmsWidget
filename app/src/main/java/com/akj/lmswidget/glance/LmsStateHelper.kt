package com.akj.lmswidget.glance

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import java.text.SimpleDateFormat


object WidgetStateHelper {
    fun save(prefs : MutablePreferences, state : LmsTop5){
        prefs[stringPreferencesKey("title0")] = state.first.title
        prefs[stringPreferencesKey("subjt0")] = state.first.subjt
        prefs[stringPreferencesKey("dDay0")] = state.first.dDay
        prefs[stringPreferencesKey("date0")] = state.first.date

        prefs[stringPreferencesKey("title1")] = state.second.title
        prefs[stringPreferencesKey("subjt1")] = state.second.subjt
        prefs[stringPreferencesKey("dDay1")] = state.second.dDay
        prefs[stringPreferencesKey("date1")] = state.second.date

        prefs[stringPreferencesKey("title2")] = state.third.title
        prefs[stringPreferencesKey("subjt2")] = state.third.subjt
        prefs[stringPreferencesKey("dDay2")] = state.third.dDay
        prefs[stringPreferencesKey("date2")] = state.third.date

        prefs[stringPreferencesKey("title3")] = state.fourth.title
        prefs[stringPreferencesKey("subjt3")] = state.fourth.subjt
        prefs[stringPreferencesKey("dDay3")] = state.fourth.dDay
        prefs[stringPreferencesKey("date3")] = state.fourth.date


        prefs[stringPreferencesKey("title4")] = state.fifth.title
        prefs[stringPreferencesKey("subjt4")] = state.fifth.subjt
        prefs[stringPreferencesKey("dDay4")] = state.fifth.dDay
        prefs[stringPreferencesKey("date4")] = state.fifth.date

        prefs[intPreferencesKey("count")] = state.count


        val currentTime = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("yyyy.MM.dd HH:mm").format(currentTime)
        prefs[stringPreferencesKey("time")] = timeFormat
    }


    fun getState(prefs : Preferences) : LmsTop5 {
        val title0 = prefs[stringPreferencesKey("title0")] ?: "Error : 다시 로그인해주세요"
        val subjt0 = prefs[stringPreferencesKey("subjt0")] ?: ""
        val dDay0 = prefs[stringPreferencesKey("dDay0")] ?: ""
        val date0 = prefs[stringPreferencesKey("date0")] ?: ""

        val title1 = prefs[stringPreferencesKey("title1")] ?: ""
        val subjt1 = prefs[stringPreferencesKey("subjt1")] ?: ""
        val dDay1 = prefs[stringPreferencesKey("dDay1")] ?: ""
        val date1 = prefs[stringPreferencesKey("date1")] ?: ""

        val title2 = prefs[stringPreferencesKey("title2")] ?: ""
        val subjt2 = prefs[stringPreferencesKey("subjt2")] ?: ""
        val dDay2 = prefs[stringPreferencesKey("dDay2")] ?: ""
        val date2 = prefs[stringPreferencesKey("date2")] ?: ""

        val title3 = prefs[stringPreferencesKey("title3")] ?: ""
        val subjt3 = prefs[stringPreferencesKey("subjt3")] ?: ""
        val dDay3 = prefs[stringPreferencesKey("dDay3")] ?: ""
        val date3 = prefs[stringPreferencesKey("date3")] ?: ""

        val title4 = prefs[stringPreferencesKey("title4")] ?: ""
        val subjt4 = prefs[stringPreferencesKey("subjt4")] ?: ""
        val dDay4 = prefs[stringPreferencesKey("dDay4")] ?: ""
        val date4 = prefs[stringPreferencesKey("date4")] ?: ""

        val lms0 = LmsData(title0, subjt0, dDay0, date0)
        val lms1 = LmsData(title1, subjt1, dDay1, date1)
        val lms2 = LmsData(title2, subjt2, dDay2, date2)
        val lms3 = LmsData(title3, subjt3, dDay3, date3)
        val lms4 = LmsData(title4, subjt4, dDay4, date4)
        val count = prefs[intPreferencesKey("count")] ?: -1


        return LmsTop5(lms0, lms1, lms2, lms3, lms4, count)
    }

    fun getTime(prefs : Preferences) : String {
        return prefs[stringPreferencesKey("time")] ?: "0000.00.00 00:00"
    }
}