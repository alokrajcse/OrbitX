package com.example.orbitx.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.orbitx.R
import com.example.orbitx.views.border
import com.example.orbitx.views.backgroundTransparent

@Composable
fun SignUpScreen() {
    val customColor = Color(0xFF121212)
    val urbanistLight = FontFamily(Font(R.font.urbanist_light))
    val urbanistRegular = FontFamily(Font(R.font.urbanist_regular))
    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logoclear_one),
                        contentDescription = null,
                        modifier = Modifier
                            .size(180.dp)
                            .padding(18.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Create your account",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = urbanistRegular,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(70.dp))
                    var name by remember { mutableStateOf(TextFieldValue("")) }
//                    BasicTextField(
//                        value = name,
//                        onValueChange = { name = it },
//                        modifier = Modifier
//                            .padding(horizontal = 38.dp)
//                            .backgroundTransparent()
//                            .border(underline = true, color = customColor)
//                            .align(Alignment.CenterHorizontally),
//                        textStyle = androidx.compose.ui.text.TextStyle(
//                            color = Color.Black,
//                            fontFamily = urbanistLight,
//                            fontSize = 14.sp
//                        ),
//                        decorationBox = { innerTextField ->
//                            Box(modifier = Modifier.fillMaxWidth()) {
//                                if (name.text.isEmpty()) {
//                                    Text(
//                                        text = "Name",
//                                        color = customColor,
//                                        fontFamily = urbanistLight,
//                                        fontSize = 14.sp
//                                    )
//                                }
//                                innerTextField()
//                            }
//                        }
//                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .padding(horizontal = 38.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontFamily = urbanistLight,
                            fontSize = 14.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Name",
                                color = customColor,
                                fontFamily = urbanistLight,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    var email by remember { mutableStateOf(TextFieldValue("")) }
//                    BasicTextField(
//                        value = email,
//                        onValueChange = { email = it },
//                        modifier = Modifier
//                            .padding(horizontal = 38.dp)
//                            .backgroundTransparent()
//                            .border(underline = true, color = customColor)
//                            .align(Alignment.CenterHorizontally),
//                        textStyle = androidx.compose.ui.text.TextStyle(
//                            color = Color.Black,
//                            fontFamily = urbanistLight,
//                            fontSize = 14.sp
//                        ),
//                        decorationBox = { innerTextField ->
//                            Box(modifier = Modifier.fillMaxWidth()) {
//                                if (email.text.isEmpty()) {
//                                    Text(
//                                        text = "Email address",
//                                        color = customColor,
//                                        fontFamily = urbanistLight,
//                                        fontSize = 14.sp
//                                    )
//                                }
//                                innerTextField()
//                            }
//                        }
//                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .padding(horizontal = 38.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontFamily = urbanistLight,
                            fontSize = 14.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Email address",
                                color = customColor,
                                fontFamily = urbanistLight,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    var password by remember { mutableStateOf(TextFieldValue("")) }
//                    BasicTextField(
//                        value = password,
//                        onValueChange = { password = it },
//                        modifier = Modifier
//                            .padding(horizontal = 38.dp)
//                            .backgroundTransparent()
//                            .border(underline = true, color = customColor)
//                            .align(Alignment.CenterHorizontally),
//                        textStyle = androidx.compose.ui.text.TextStyle(
//                            color = Color.Black,
//                            fontFamily = urbanistLight,
//                            fontSize = 14.sp
//                        ),
//                        decorationBox = { innerTextField ->
//                            Box(modifier = Modifier.fillMaxWidth()) {
//                                if (password.text.isEmpty()) {
//                                    Text(
//                                        text = "Password",
//                                        color = customColor,
//                                        fontFamily = urbanistLight,
//                                        fontSize = 14.sp
//                                    )
//                                }
//                                innerTextField()
//                            }
//                        }
//                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .padding(horizontal = 38.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontFamily = urbanistLight,
                            fontSize = 14.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Password",
                                color = customColor,
                                fontFamily = urbanistLight,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    val customButtonColor = Color(0xFF492468)
                    Button(
                        onClick = { /* Handle Log In */ },
                        colors = ButtonDefaults.buttonColors(containerColor = customButtonColor),
                        modifier = Modifier
                            .width(150.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(0.dp),
                    ) {
                        Text(text = "Sign Up", color = Color.White, fontFamily = urbanistMedium, fontSize = 17.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Forgot Password?",
                        fontFamily = urbanistLight,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable { /* Handle Forgot Password */ }
                            .padding(vertical = 8.dp)
                            .border(underline = true, color = customColor)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(50.dp)) // Increase the height here to move the Row up
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have account?",
                        fontFamily = urbanistLight,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val customButtonColor = Color(0xFF492468)
                    Text(
                        text = "Log In",
                        fontFamily = urbanistLight,
                        fontSize = 14.sp,
                        color = customButtonColor,
                        modifier = Modifier.clickable { /* Handle Create Account */ }
                    )
                }
            }
        }
    )
}
