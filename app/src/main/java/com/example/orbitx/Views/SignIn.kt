package com.example.orbitx.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orbitx.R
import com.example.orbitx.ViewModel.AuthViewModel

@Composable
fun SignInScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val urbanistLight = FontFamily(Font(R.font.urbanist_light))
    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))
    val customColor = Color(0xFF121212)
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
                            .size(190.dp)
                            .padding(18.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Sign In",
                        fontSize = 30.sp,
                        color = Color.Black,
                        fontFamily = urbanistMedium
                    )
                    Spacer(modifier = Modifier.height(70.dp))
                    var email by remember { mutableStateOf(TextFieldValue("")) }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .padding(horizontal = 38.dp)
                            .fillMaxWidth(),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 14.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Email address",
                                color = customColor,
                                fontSize = 14.sp,
                                fontFamily = urbanistLight
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    var password by remember { mutableStateOf(TextFieldValue("")) }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .padding(horizontal = 38.dp)
                            .fillMaxWidth(),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 14.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Password",
                                color = customColor,
                                fontSize = 14.sp,
                                fontFamily = urbanistLight
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    val customButtonColor = Color(0xFF492468)
                    Button(
                        onClick = { viewModel.signIn(email.text, password.text, onNavigateToHome) },
                        colors = ButtonDefaults.buttonColors(containerColor = customButtonColor),
                        modifier = Modifier
                            .width(150.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(0.dp),
                    ) {
                        Text(text = "Log In", color = Color.White, fontSize = 17.sp, fontFamily = urbanistLight)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Forgot Password?",
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable { /* Handle Forgot Password */ }
                            .padding(vertical = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        fontFamily = urbanistLight
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Still without account?",
                        fontSize = 14.sp,
                        fontFamily = urbanistLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val customButtonColor = Color(0xFF492468)
                    Text(
                        text = "Create one",
                        fontSize = 14.sp,
                        color = customButtonColor,
                        modifier = Modifier.clickable(onClick = onNavigateToRegister),
                        fontFamily = urbanistLight
                    )
                }
            }
        }
    )
}
