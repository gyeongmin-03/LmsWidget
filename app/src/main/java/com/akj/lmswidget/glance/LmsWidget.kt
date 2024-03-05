package com.akj.lmswidget.glance

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
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
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.akj.lmswidget.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat


class LmsWidget : GlanceAppWidget() {

    companion object {  //가로, 세로
        private val thinMode = DpSize(100.dp, 90.dp)   //2x2

        private val smallNarrowMode = DpSize(220.dp, 180.dp)
        private val smallWideMode = DpSize(320.dp, 180.dp)

        private val mediumNarrowMode = DpSize(220.dp, 270.dp)
        private val mediumWideMode = DpSize(320.dp, 270.dp)

        private val largeNarrowMode = DpSize(220.dp, 360.dp)
        private val largeWideMode = DpSize(320.dp, 360.dp)  //5x4
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(thinMode, smallNarrowMode ,smallWideMode, mediumNarrowMode ,mediumWideMode, largeNarrowMode, largeWideMode)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    private fun Content() {
        val myData = WidgetStateHelper.getState(currentState())
        val timeFormat = WidgetStateHelper.getTime(currentState())

        val size = LocalSize.current
        GlanceTheme {
            when (size) {
                thinMode -> LmsThin(myData, timeFormat)

                smallNarrowMode -> LmsSmallNarrow(myData, timeFormat)
                smallWideMode -> LmsSmallWide(myData, timeFormat)

                mediumNarrowMode -> LmsMediumNarrow(myData, timeFormat)
                mediumWideMode -> LmsMediumWide(myData, timeFormat)

                largeNarrowMode -> LmsLargeNarrow(myData, timeFormat)
                largeWideMode -> LmsLargeWide(myData, timeFormat)
            }
        }
    }
}


@Composable
fun LmsThin(myData: LmsTop5, time: String){
    AppWidgetColumn {
        Text(
            "총 과제 수",
            modifier = GlanceModifier.fillMaxWidth(),
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                color = ColorProvider(Color.Black, Color.White)
            )
        )
        Text(
            if(myData.count < 0) "Error" else "${myData.count}",
            modifier = GlanceModifier.fillMaxWidth(),
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = ColorProvider(Color.Black, Color.White),
                fontWeight = FontWeight.Bold,
            )
        )
        LatestUpdate(time, GlanceModifier.fillMaxWidth(), 9.sp, Alignment.Horizontal.CenterHorizontally, myData = myData, false)
    }
}



@Composable
fun LmsSmallWide(myData: LmsTop5, time: String){
    AppWidgetColumn {
        WideTextBox(myData.first)
        WideTextBox(myData.second)
        LatestUpdate("$time 갱신", myData = myData)
    }
}

@Composable
fun LmsSmallNarrow(myData: LmsTop5, time: String){
    AppWidgetColumn {
        NarrowTextBox(myData.first)
        NarrowTextBox(myData.second)
        LatestUpdate("$time 갱신", myData = myData)
    }
}


@Composable
fun LmsMediumWide(myData: LmsTop5, time: String){
    AppWidgetColumn {
        WideTextBox(myData.first)
        WideTextBox(myData.second)
        WideTextBox(myData.third)
        LatestUpdate("$time 갱신", myData = myData)
    }
}

@Composable
fun LmsMediumNarrow(myData: LmsTop5, time: String) {
    AppWidgetColumn {
        NarrowTextBox(myData.first)
        NarrowTextBox(myData.second)
        NarrowTextBox(myData.third)
        LatestUpdate("$time 갱신", myData = myData)
    }
}


@Composable
fun LmsLargeNarrow(myData: LmsTop5, time: String){
    AppWidgetColumn {
        NarrowTextBox(myData.first)
        NarrowTextBox(myData.second)
        NarrowTextBox(myData.third)
        NarrowTextBox(myData.fourth)
        NarrowTextBox(myData.fifth)
        LatestUpdate("$time 갱신", myData = myData)
    }
}


@Composable
fun LmsLargeWide(myData: LmsTop5, time: String){
    AppWidgetColumn {
        WideTextBox(myData.first)
        WideTextBox(myData.second)
        WideTextBox(myData.third)
        WideTextBox(myData.fourth)
        WideTextBox(myData.fifth)
        LatestUpdate("$time 갱신", myData = myData)
    }
}



@Composable
fun NarrowTextBox(data: LmsData){
    Row(modifier = GlanceModifier
        .background(ImageProvider(R.drawable.widget_border))
        .fillMaxWidth()
        .height(75.dp)
        .padding(8.dp)
    ) {
        Column(modifier = GlanceModifier.width(200.dp)) {
            Text(
                text = data.title,
                maxLines = 1,
                style = TextStyle(fontSize = 12.sp, color = ColorProvider(Color.Black, Color.White))
            )
            Row{
                Text(
                    text = data.dDay,
                    style = TextStyle(fontSize = 17.sp, color = ColorProvider(Color.Black, Color.White))
                )
                Text(
                    text = if(data.date != "") {data.date.substring(5) +" 까지"} else "",
                    maxLines = 1,
                    style = TextStyle(fontSize = 11.sp, color = ColorProvider(Color.Black, Color.White)),
                    modifier = GlanceModifier.padding(start = 10.dp).fillMaxWidth()
                )
            }
        }
    }
}



@Composable
fun WideTextBox(data: LmsData){
    Row(modifier = GlanceModifier
        .background(ImageProvider(R.drawable.widget_border))
        .fillMaxWidth()
        .height(75.dp)
        .padding(8.dp)
    ) {
        Column(modifier = GlanceModifier.width(200.dp)) {
            Text(
                text = data.subjt,
                maxLines = 1,
                style = TextStyle(fontSize = 10.sp, color = ColorProvider(Color.Black, Color.White))
            )
            Text(
                text = data.title,
                maxLines = 2,
                style = TextStyle(fontSize = 12.sp, color = ColorProvider(Color.Black, Color.White))
            )
        }
        Column(modifier = GlanceModifier.width(100.dp).padding(start = 5.dp)) {
            Text(
                text = data.dDay,
                style = TextStyle(fontSize = 15.sp, color = ColorProvider(Color.Black, Color.White))
            )
            Text(
                text = if(data.date != "") {data.date.replace(" ", "\n") +" 까지"} else "",
                maxLines = 2,
                style = TextStyle(fontSize = 10.sp, color = ColorProvider(Color.Black, Color.White))
            )
        }
    }
}

@Composable
fun LatestUpdate(
    time:String,
    modifier: GlanceModifier = GlanceModifier.padding(top = 8.dp),
    fontSize: TextUnit = 10.sp,
    alignment: Alignment.Horizontal = Alignment.Horizontal.Start,
    myData: LmsTop5,
    visible : Boolean = true
){
    val currentTimeState = SimpleDateFormat("mm").format(System.currentTimeMillis())
    val timeState = time.split(":")[1].split(" ")[0]
    
    val onClick =
        actionRunCallback<UpdateLmsData>()


    Row(modifier = modifier, horizontalAlignment = alignment){
        Text(time, style = TextStyle(fontSize = fontSize, color = ColorProvider(Color.DarkGray, Color.LightGray)))
        if(visible){
            Text(" / 총 과제 : ${myData.count} 개",
                style = TextStyle(fontSize = fontSize, color = ColorProvider(Color.DarkGray, Color.LightGray))
            )
        }
        Image(
            provider = ImageProvider(R.drawable.refresh),
            contentDescription = "Refresh",
            modifier = GlanceModifier
                .clickable(onClick)
                .padding(start = 3.dp),
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
            val sharedPreferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE)
            val time = sharedPreferences.getString("time", "00")
            val currentTime = SimpleDateFormat("MM.dd HH:mm").format(System.currentTimeMillis())

            if(time!! != currentTime){
                val workRequest = OneTimeWorkRequestBuilder<LmsWorker>().build()
                WorkManager.getInstance(context).enqueue(workRequest)   //worker 실행

                val editor = sharedPreferences.edit()
                editor.putString("time", SimpleDateFormat("MM.dd HH:mm").format(System.currentTimeMillis()))
                editor.apply()

                runBlocking {
                    delay(3000)
                }

                LmsWidget().update(context, glanceId)   //내용이 바뀌었을 때만 실행됨
            }
        } catch (e: Exception){
            Log.e("ActionCallback에러", "에러 내용: ${e.message}")
        }
    }
}