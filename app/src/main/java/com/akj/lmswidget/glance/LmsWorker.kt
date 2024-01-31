package com.akj.lmswidget.glance

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class LmsWorker(
    private val context: Context,
    workerParam : WorkerParameters
) : CoroutineWorker(context, workerParam) {


    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(LmsWidget::class.java)

        return try {
            setWidgetState(glanceIds, LmsRepo.getLmsData())
            Log.d("DDDD", "doWork() 실행")
            Result.success()
        } catch (e : Exception){
            if(runAttemptCount < 10) {
                Result.retry()
            }
            else {
                Result.failure()
            }
        }
    }


    private suspend fun setWidgetState(glanceIds: List<GlanceId>, myData: LmsData) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, glanceId){
                WidgetStateHelper.save(it, myData)
            }
        }
    }
}

object WidgetStateHelper {
    fun save(prefs : MutablePreferences, state : LmsData){
        prefs[stringPreferencesKey("first")] = state.first
        prefs[stringPreferencesKey("second")] = state.second
        prefs[stringPreferencesKey("third")] = state.third
        prefs[stringPreferencesKey("fourth")] = state.fourth
        prefs[stringPreferencesKey("fifth")] = state.fifth
    }

    fun getState(prefs : Preferences) : LmsData {
        val first = prefs[stringPreferencesKey("first")] ?: ""
        val second = prefs[stringPreferencesKey("second")] ?: ""
        val third = prefs[stringPreferencesKey("third")] ?: ""
        val fourth = prefs[stringPreferencesKey("fourth")] ?: ""
        val fifth = prefs[stringPreferencesKey("fifth")] ?: ""

        return LmsData(first,second,third,fourth,fifth)
    }
}