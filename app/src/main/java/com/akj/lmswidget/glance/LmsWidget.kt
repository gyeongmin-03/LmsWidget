package com.akj.lmswidget.glance

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.Text
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager


class LmsWidget : GlanceAppWidget() {
    companion object {
        private val thinMode = DpSize(120.dp, 120.dp)
        private val smallMode = DpSize(184.dp, 184.dp)
        private val mediumMode = DpSize(260.dp, 200.dp)
        private val largeMode = DpSize(260.dp, 280.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(thinMode, smallMode, mediumMode, largeMode)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    private fun Content() {
        val myData = WidgetStateHelper.getState(currentState())
        Log.d("DDDD", "Content() 실행")

        val size = LocalSize.current
        GlanceTheme {
//            LmsTest()
            LmsLarge(myData)
            when (size) {
//                thinMode -> LmsThin()
//                smallMode -> LmsSmall()
//                mediumMode -> LmsMedium()
//                largeMode -> LmsLarge(myData)
            }

        }
    }
}

@Composable
fun LmsLarge(myData: LmsData){
    AppWidgetColumn() {
        Text(text = myData.first, modifier = GlanceModifier.fillMaxWidth())
        Text(text = myData.second, modifier = GlanceModifier.fillMaxWidth())
        Text(text = myData.third, modifier = GlanceModifier.fillMaxWidth())
        Text(text = myData.fourth, modifier = GlanceModifier.fillMaxWidth())
        Text(text = myData.fifth, modifier = GlanceModifier.fillMaxWidth())
        Button("다시 로딩", actionRunCallback<UpdateLmsAction>())
    }
}





class UpdateLmsAction : ActionCallback{
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val workRequest = OneTimeWorkRequestBuilder<LmsWorker>().build()
            WorkManager.getInstance(context).enqueue(workRequest)

            Thread.sleep(3*1000L)

            LmsWidget().update(context, glanceId)   //내용이 바뀌었을 때만 실행됨
        } catch (e: Exception){
            Log.e("ActionCallback에러", "에러 내용: ${e.message}")
        }
    }
}