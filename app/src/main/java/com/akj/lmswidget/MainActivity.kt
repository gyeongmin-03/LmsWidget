package com.akj.lmswidget

import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import com.akj.lmswidget.ui.theme.LmsWidgetTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LmsWidgetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WriteData()
                }
            }
        }
    }
}

@Composable
fun WriteData() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        var Id by remember { mutableStateOf("") }
        var pwd by remember { mutableStateOf("")}


        Column {
            TextField(
                value = Id,
                onValueChange = { Id = it},
                )
            TextField(
                value = pwd,
                onValueChange = {pwd = it}
            )

            Button(
                onClick = {},
                content = {Text("로그인")}
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}