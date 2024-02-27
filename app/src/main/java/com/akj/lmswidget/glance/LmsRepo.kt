package com.akj.lmswidget.glance

import android.content.SharedPreferences
import android.util.Log
import org.jsoup.Connection
import org.jsoup.Jsoup


object LmsRepo {
    fun getLmsCookie(id : String?, pwd : String?) : Map<String, String>?{
        if(id.isNullOrEmpty() || pwd.isNullOrEmpty()){
            Log.d("getLmsData() 오류", "id 혹은 pwd의 로컬데이터를 가져오지 못함")
            return null
        }

        val data = hashMapOf(
            "usr_id" to id,
            "usr_pwd" to pwd
        )

        // 로그인 페이지 접속
        val login = Jsoup.connect("https://lms.pknu.ac.kr/ilos/lo/login.acl")
            .timeout(5000)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Whale/3.24.223.21 Safari/537.36")
            .data(data)
            .method(Connection.Method.POST).execute()

        return login.cookies()
    }

    fun getLmsData(shared: SharedPreferences) : LmsTop5 {
        val id = shared.getString("id", "")
        val pwd = shared.getString("pwd", "")

        val cookie = getLmsCookie(id, pwd)
        if(cookie.isNullOrEmpty()){
            val nullDataStr = LmsData("Error : 다시 로그인해주세요", "" ,"" ,"" )
            val nullData = LmsData("","","","")

            return LmsTop5(nullDataStr, nullData, nullData, nullData, nullData, -1)
        }

        val popData2 = hashMapOf(
            "todoKjList" to "",
            "chk_cate" to "ALL",
            "encoding" to "utf-8"
        )


        val todoList = Jsoup.connect("https://lms.pknu.ac.kr/ilos/mp/todo_list.acl")
            .timeout(5000)
            .cookies(cookie)
            .data(popData2)
            .method(Connection.Method.POST).execute().parse()

        val NoTodo = "LMS에 과제가 없습니다"

        var title0 = todoList.select(".todo_wrap:eq(0) .todo_title").text()
        if(title0 == "") title0 = NoTodo
        val subjt0 = todoList.select(".todo_wrap:eq(0) .todo_subjt").text()
        val dDay0 = todoList.select(".todo_wrap:eq(0) .todo_date .todo_d_day").text()
        val date0 = todoList.select(".todo_wrap:eq(0) .todo_date .todo_date").text().replace(" ", "\n")
        val lms0 = LmsData(title0, subjt0, dDay0, date0)



        val title1 = todoList.select(".todo_wrap:eq(1) .todo_title").text()
        val subjt1 = todoList.select(".todo_wrap:eq(1) .todo_subjt").text()
        val dDay1 = todoList.select(".todo_wrap:eq(1) .todo_date .todo_d_day").text()
        val date1 = todoList.select(".todo_wrap:eq(1) .todo_date .todo_date").text().replace(" ", "\n")
        val lms1 = LmsData(title1, subjt1, dDay1, date1)


        val title2 = todoList.select(".todo_wrap:eq(2) .todo_title").text()
        val subjt2 = todoList.select(".todo_wrap:eq(2) .todo_subjt").text()
        val dDay2 = todoList.select(".todo_wrap:eq(2) .todo_date .todo_d_day").text()
        val date2 = todoList.select(".todo_wrap:eq(2) .todo_date .todo_date").text().replace(" ", "\n")
        val lms2 = LmsData(title2, subjt2, dDay2, date2)


        val title3 = todoList.select(".todo_wrap:eq(3) .todo_title").text()
        val subjt3 = todoList.select(".todo_wrap:eq(3) .todo_subjt").text()
        val dDay3 = todoList.select(".todo_wrap:eq(3) .todo_date .todo_d_day").text()
        val date3 = todoList.select(".todo_wrap:eq(3) .todo_date .todo_date").text().replace(" ", "\n")
        val lms3 = LmsData(title3, subjt3, dDay3, date3)


        val title4 = todoList.select(".todo_wrap:eq(4) .todo_title").text()
        val subjt4 = todoList.select(".todo_wrap:eq(4) .todo_subjt").text()
        val dDay4 = todoList.select(".todo_wrap:eq(4) .todo_date .todo_d_day").text()
        val date4 = todoList.select(".todo_wrap:eq(4) .todo_date .todo_date").text().replace(" ", "\n")
        val lms4 = LmsData(title4, subjt4, dDay4, date4)

        val todoCount =
            if(title0.equals(NoTodo)) {
                0
            }else{
                todoList.select(".todo_wrap").size
            }

        return LmsTop5(lms0, lms1, lms2, lms3, lms4, todoCount)
    }
}
