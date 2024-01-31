package com.akj.lmswidget.glance

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.akj.lmswidget.glance.LmsWidget

class LmsWidgetReceiver : GlanceAppWidgetReceiver(){
    override val glanceAppWidget = LmsWidget()
}