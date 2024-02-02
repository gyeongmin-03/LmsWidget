package com.akj.lmswidget.glance

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class LmsWidgetReceiver : GlanceAppWidgetReceiver(){
    override val glanceAppWidget = LmsWidget()
}