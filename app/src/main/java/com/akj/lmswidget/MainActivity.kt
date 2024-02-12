package com.akj.lmswidget

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akj.lmswidget.glance.LmsRepo
import com.akj.lmswidget.ui.theme.LmsWidgetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val shared = getSharedPreferences("userData", MODE_PRIVATE)
        val defaultLogin = shared.getBoolean("login", false)

        setContent {
            LmsWidgetTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainAct(shared, defaultLogin)
                }
            }
        }
    }
}

@Composable
fun MainAct(shared: SharedPreferences, defaultLogin : Boolean){
    val editor = shared.edit()

    var login by remember {
        mutableStateOf(defaultLogin)
    }

    if(login)  { ReadUserData(shared){login = false} }
    else { WriteUserData(editor){login = true} }
}


@Composable
fun ReadUserData(shared : SharedPreferences, command : () -> Unit){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column {
            val id = shared.getString("id", "")
            val pwd = shared.getString("pwd", "")
            val editor = shared.edit()

            if (id.isNullOrEmpty() || pwd.isNullOrEmpty()){
                Text("ReadUserData 오류")
                Text("id 또는 pwd를 로컬데이터에서 가져오지 못함")
                Text("다시 로그인 해주세요")
            } else{
                Text("로그인 한 학번 :")
                Text(id)
            }

            Button(
                onClick = {
                    editor.putString("id", "")
                    editor.putString("pwd", "")
                    editor.putBoolean("login", false)
                    command()
                    editor.apply()
                },
                content = {Text("다시 설정")}
            )
        }
    }
}



@Composable
fun WriteUserData(editor : SharedPreferences.Editor, command : () -> Unit) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        var id by remember { mutableStateOf("") }
        var pwd by remember { mutableStateOf("")}
        var passwordVisibility by remember { mutableStateOf(false) }


        Column {
            TextField(
                value = id,
                onValueChange = { id = it},
                placeholder = {
                        Text("학번")
                    }
                )
            Box{
                TextField(
                    value = pwd,
                    onValueChange = {pwd = it},
                    placeholder = {
                        Text("비밀번호")
                    },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
                )
                IconButton(
                    onClick = { passwordVisibility = !passwordVisibility },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = if(passwordVisibility) painterResource(R.drawable.visibility) else painterResource(R.drawable.visibility_off),
                        contentDescription = ""
                    )
                }
            }


            Button(
                onClick = {
                    if (isCorrect(id, pwd, context)) {
                        performLogin(id, pwd, editor, command, context)
                    }
                },
                content = { Text("로그인") }
            )
        }
    }
}


fun isCorrect(id: String, pwd : String, context : Context) : Boolean{
    if(id.isBlank()){
        Toast.makeText(context, "학번을 바르게 입력해주세요", Toast.LENGTH_LONG).show()
        return false
    }
    else if (pwd.isBlank()){
        Toast.makeText(context, "비밀번호를 바르게 입력해주세요", Toast.LENGTH_LONG).show()
        return false
    }

    return true
}


private fun performLogin(id: String, pwd: String, editor: SharedPreferences.Editor, command: () -> Unit, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val isSuccess = isSucceedLogin(id, pwd)
        withContext(Dispatchers.Main) {
            if (isSuccess) {
                editor.putString("id", id)
                editor.putString("pwd", pwd)
                editor.putBoolean("login", true)
                command()
                editor.commit()
            } else{
                Toast.makeText(context, "로그인에 실패하였습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

}


suspend fun isSucceedLogin(id: String, pwd: String) : Boolean{
    val cookie = LmsRepo.getLmsCookie(id, pwd)

    //메인 페이지 접속
    val mainPage = Jsoup
        .connect("https://lms.pknu.ac.kr/ilos/main/main_form.acl")
        .cookies(cookie)
        .get()

    if ("로그아웃" !in mainPage.html()){
        return false
    }

    return true
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp)

        ) {
            Column(modifier = Modifier.width(300.dp)) {
                Text(
                    text = "data.subjt",
                    maxLines = 1,
                    fontSize = 15.sp
                )
                Text(
                    text = "data.titaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaale",
                    maxLines = 2,
                    fontSize = 17.sp
                )
            }
            Column(modifier = Modifier
                .width(100.dp)
                .padding(start = 8.dp)) {
                Text(
                    text = "data.dDay",
                    fontSize = 20.sp
                )
                Text(
                    text = "2022.01.01\n12:12 까지",
                    maxLines = 3,
                    fontSize = 13.sp
                )
            }
        }

        Row {
            Text("최근 갱신 : time")
        }
    }



}