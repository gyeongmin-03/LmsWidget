package com.akj.lmswidget

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akj.lmswidget.glance.LmsRepo
import com.akj.lmswidget.ui.theme.DarkBlue
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
                MainAct(shared, defaultLogin)
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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(bottom = 20.dp)){
                Image(
                    painter = painterResource(R.drawable.lms_widget_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(10.dp),
                    alignment = Alignment.Center
                )
                Text(
                    text = "부경대학교\n과제 위젯도우미",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            if(login)  { ReadUserData(shared){login = false} }
            else { WriteUserData(editor){login = true} }
        }
    }
}


@Composable
fun ReadUserData(shared : SharedPreferences, command : () -> Unit){
    val id = shared.getString("id", "")
    val pwd = shared.getString("pwd", "")
    val editor = shared.edit()

    val error = """
        ReadUserData 오류
        id 또는 pwd를 로컬데이터에서 가져오지 못함
        다시 로그인 해주세요
    """.trimIndent()

    val explain = """
        로그인 되었습니다.
        바탕화면에 위젯을 생성하고
        새로고침을 진행해주세요.
        
        필요에 따라서 위젯의 크기를
        줄이거나 늘릴 수 있습니다.
        
        Error가 나타날 시
        "다시 설정" 버튼을 누른 후
        다시 로그인 해주세요.
    """.trimIndent()

    if (id.isNullOrEmpty() || pwd.isNullOrEmpty()){
        Text(error)
    } else{
        Text(explain, textAlign = TextAlign.Center, fontSize = 15.sp, color = Color.DarkGray)
        Text("현재 학번 :\n$id", textAlign = TextAlign.Center, fontSize = 20.sp,modifier = Modifier.padding(top = 20.dp))
    }

    Button(
        onClick = {
            editor.putString("id", "")
            editor.putString("pwd", "")
            editor.putBoolean("login", false)
            command()
            editor.apply()
        },
        content = {Text("다시 설정")},
        colors = ButtonDefaults.buttonColors(backgroundColor = DarkBlue, contentColor = Color.White),
        modifier = Modifier.padding(top = 5.dp)
    )
}



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WriteUserData(editor : SharedPreferences.Editor, command : () -> Unit) {
    val context = LocalContext.current
    var id by remember { mutableStateOf("") }
    var pwd by remember { mutableStateOf("")}
    var passwordVisibility by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val explain = """
        로그인 후 위젯을 
        새로고침 해주세요.
    """.trimIndent()

    Text(explain, textAlign = TextAlign.Center, fontSize = 15.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 15.dp))
    TextField(
        value = id,
        onValueChange = { id = it},
        placeholder = {
            Text("학번")
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() }),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Gray,
            cursorColor = DarkBlue,
            backgroundColor = Color.White
        ),
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier.border(width = 2.dp, color = DarkBlue)
    )
    Box{
        TextField(
            value = pwd,
            onValueChange = {pwd = it},
            modifier = Modifier.padding(top = 8.dp, bottom = 10.dp).border(width = 2.dp, color = DarkBlue),
            placeholder = {
                Text("비밀번호")
            },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {dataCheck(id, pwd, editor, command, context)}),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Gray,
                cursorColor = DarkBlue,
                backgroundColor = Color.White
            ),
            shape = RoundedCornerShape(0.dp)
        )

        IconButton(
            onClick = { passwordVisibility = !passwordVisibility },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = if(passwordVisibility) painterResource(R.drawable.visibility) else painterResource(R.drawable.visibility_off),
                contentDescription = null
            )
        }
    }


    Button(
        onClick = {
            dataCheck(id, pwd, editor, command, context)
        },
        content = { Text("로그인") },
        colors = ButtonDefaults.buttonColors(backgroundColor = DarkBlue, contentColor = Color.White)
    )
}

fun dataCheck(id: String, pwd : String, editor: Editor, command: () -> Unit, context : Context){
    if (isCorrect(id, pwd, context)) {
        performLogin(id, pwd, editor, command, context)
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