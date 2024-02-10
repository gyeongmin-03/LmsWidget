package com.akj.lmswidget.glance

data class LmsData(
    val title : String,
    val subjt : String,
    val dDay : String,
    val date : String
)

data class LmsTop5(
    val first: LmsData,
    val second: LmsData,
    val third: LmsData,
    val fourth: LmsData,
    val fifth: LmsData,
)