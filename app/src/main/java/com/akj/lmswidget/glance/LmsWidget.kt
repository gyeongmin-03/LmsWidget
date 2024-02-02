package com.akj.lmswidget.glance

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.*
import androidx.glance.text.*
import androidx.glance.layout.*
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
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

            when (size) {
//                thinMode -> LmsThin()
                thinMode -> Test()
//                smallMode -> LmsSmall()
                smallMode -> Test()
//                mediumMode -> LmsMedium()
                mediumMode -> Test()
                largeMode -> LmsLarge(myData)
            }

        }
    }
}

@Composable
fun Test(){
    Text("준비 중")
}


@Composable
fun LmsLarge(myData: LmsTop5){
    AppWidgetColumn {
        LargeTextBox(myData.first)
        LargeTextBox(myData.second)
        LargeTextBox(myData.third)
        LargeTextBox(myData.fourth)
        LargeTextBox(myData.fifth)
        Button("다시 로딩", actionRunCallback<UpdateLmsAction>())
    }
}
@Composable
fun LargeTextBox(data: LmsData){
    Row(modifier = GlanceModifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(8.dp)
    ) {
        Column(modifier = GlanceModifier.width(300.dp)) {
            Text(
                text = data.subjt,
                maxLines = 1,
                style = TextStyle(fontSize = 15.sp)
            )
            Text(
                text = data.title,
                maxLines = 2,
                style = TextStyle(fontSize = 17.sp)
            )
        }
        Column(modifier = GlanceModifier.width(100.dp)) {
            Text(
                text = data.dDay,
                style = TextStyle(fontSize = 23.sp)
            )
            Text(
                text = data.date +" 까지",
                maxLines = 3,
                style = TextStyle(fontSize = 13.sp)
            )
        }
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