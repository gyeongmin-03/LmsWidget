package com.akj.lmswidget

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
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
import androidx.compose.ui.tooling.preview.Preview
import com.akj.lmswidget.ui.theme.LmsWidgetTheme


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
        val id = shared.getString("id", "")
        val pwd = shared.getString("pwd", "")
        val editor = shared.edit()

        if (id.isNullOrEmpty() || pwd.isNullOrEmpty()){
            Text("ReadUserData 오류")
            Text("id 또는 pwd를 로컬데이터에서 가져오지 못함")
            Text("다시 로그인 해주세요")
        } else{
            Column {
                Text(id)
                Text(pwd)
            }
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



@Composable
fun WriteUserData(editor : SharedPreferences.Editor, command : () -> Unit) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        var id by remember { mutableStateOf("") }
        var pwd by remember { mutableStateOf("")}


        Column {
            TextField(
                value = id,
                onValueChange = { id = it},
                placeholder = {
                        Text("학번")
                    }
                )
            TextField(
                value = pwd,
                onValueChange = {pwd = it},
                placeholder = {
                    Text("비밀번호")
                }
            )

            Button(
                onClick = {
                    if(isCorrect(id, pwd, context)){
                        editor.putString("id", id)
                        editor.putString("pwd", pwd)
                        editor.putBoolean("login", true)
                        command()
                        editor.commit()    
                    } },
                content = {Text("로그인")}
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


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}