package com.akj.lmswidget.glance

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.akj.lmswidget.R
import java.text.SimpleDateFormat



class LmsWidget : GlanceAppWidget() {

    companion object {  //가로, 세로
        private val thinMode = DpSize(100.dp, 90.dp)   //2x2
        private val smallMode = DpSize(260.dp, 180.dp)
        private val mediumMode = DpSize(260.dp, 270.dp)
        private val largeMode = DpSize(260.dp, 360.dp)  //5x4
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
        val currentTime = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("yyyy.MM.dd HH:mm").format(currentTime)

        Log.d("DDDD", "Content() 실행")

        val size = LocalSize.current
        GlanceTheme {
            when (size) {
                thinMode -> LmsThin(myData, timeFormat)
                smallMode -> LmsSmall(myData, timeFormat)
                mediumMode -> LmsMedium(myData, timeFormat)
                largeMode -> LmsLarge(myData, timeFormat)
            }
        }
    }
}


@Composable
fun LmsThin(myData: LmsTop5, time: String){
    AppWidgetColumn {
        Text(myData.first.title, maxLines = 1, style = TextStyle(fontSize = 13.sp, color = ColorProvider(Color.Black, Color.White)))
        Text(myData.first.subjt, maxLines = 1 ,style = TextStyle(fontSize = 10.sp, color = ColorProvider(Color.Black, Color.White)))
        Text(myData.first.date, maxLines = 1 ,style = TextStyle(fontSize = 10.sp, color = ColorProvider(Color.Black, Color.White)))
        Text(
            myData.first.dDay,
            modifier = GlanceModifier.fillMaxWidth(),
            style = TextStyle(
                textAlign = TextAlign.End,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        )
        LatestUpdate("최근 갱신 :\n${time.replace(" ", "\n")}")
    }
}

@Composable
fun LmsSmall(myData: LmsTop5, time: String){
    AppWidgetColumn {
        LargeTextBox(myData.first)
        LargeTextBox(myData.second)
        LatestUpdate("최근 갱신 : $time")
    }
}


@Composable
fun LmsMedium(myData: LmsTop5, time: String){
    AppWidgetColumn {
        LargeTextBox(myData.first)
        LargeTextBox(myData.second)
        LargeTextBox(myData.third)
        LatestUpdate("최근 갱신 : $time")
    }
}


@Composable
fun LmsLarge(myData: LmsTop5, time: String){
    AppWidgetColumn {
        LargeTextBox(myData.first)
        LargeTextBox(myData.second)
        LargeTextBox(myData.third)
        LargeTextBox(myData.fourth)
        LargeTextBox(myData.fifth)
        LatestUpdate("최근 갱신 : $time")
    }
}

@Composable
fun LargeTextBox(data: LmsData){
    Row(modifier = GlanceModifier
        .background(ImageProvider(R.drawable.widget_border))
        .fillMaxWidth()
        .wrapContentHeight()
//        .height(100.dp)
        .padding(8.dp)

    ) {
        Column(modifier = GlanceModifier.width(300.dp)) {
            Text(
                text = data.subjt,
                maxLines = 1,
                style = TextStyle(fontSize = 15.sp, color = ColorProvider(Color.Black, Color.White))
            )
            Text(
                text = data.title,
                maxLines = 2,
                style = TextStyle(fontSize = 17.sp, color = ColorProvider(Color.Black, Color.White))
            )
        }
        Column(modifier = GlanceModifier.width(100.dp).padding(start = 8.dp)) {
            Text(
                text = data.dDay,
                style = TextStyle(fontSize = 20.sp, color = ColorProvider(Color.Black, Color.White))
            )
            Text(
                text = if(data.date != "") {data.date +" 까지"} else "",
                maxLines = 3,
                style = TextStyle(fontSize = 13.sp, color = ColorProvider(Color.Black, Color.White))
            )
        }
    }
}

@Composable
fun LatestUpdate(text:String){
    Row(modifier = GlanceModifier.padding(top = 8.dp)){
        Text(text, style = TextStyle(fontSize = 10.sp, color = ColorProvider(Color.DarkGray, Color.LightGray)))
        Image(
            provider = ImageProvider(R.drawable.refresh),
            contentDescription = "Refresh",
            modifier = GlanceModifier.clickable(actionRunCallback<UpdateLmsData>()).padding(start = 5.dp),
            colorFilter = ColorFilter.tint(ColorProvider(Color.DarkGray, Color.LightGray))
        )
    }

}



object UpdateLmsData : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val workRequest = OneTimeWorkRequestBuilder<LmsWorker>().build()
            WorkManager.getInstance(context).enqueue(workRequest)   //worker 실행

            Thread.sleep(3*1000L)

            LmsWidget().update(context, glanceId)   //내용이 바뀌었을 때만 실행됨

        } catch (e: Exception){
            Log.e("ActionCallback에러", "에러 내용: ${e.message}")
        }
    }
}



//새로운 onClick 함수를 만든다.
//Refresh 아이콘이 돌아가도록