package com.akj.lmswidget.glance

import android.content.Context
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
        val shared = context.getSharedPreferences("userData", Context.MODE_PRIVATE)

        return try {
            setWidgetState(glanceIds, LmsRepo.getLmsData(shared))
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


    private suspend fun setWidgetState(glanceIds: List<GlanceId>, myData: LmsTop5) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, glanceId){
                WidgetStateHelper.save(it, myData)
            }
        }
    }
}