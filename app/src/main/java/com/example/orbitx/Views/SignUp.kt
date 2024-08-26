package com.example.orbitx.Views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun SignUpScreen(
    onNavigateBackToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val urbanistLight = FontFamily(Font(R.font.urbanist_light))
    val urbanistRegular = FontFamily(Font(R.font.urbanist_regular))
    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))
    val customColor = Color(0xFF121212)

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                        text = "Create your account",
                        fontSize = 30.sp,
                        fontFamily = urbanistMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(70.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier
                            .padding(horizontal = 38.dp)
                            .fillMaxWidth(),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 14.sp
                        ),
                        placeholder = {
                            Text(
                                text = "Username",
                                color = customColor,
                                fontFamily = urbanistLight,
                                fontSize = 14.sp
                            )
                        },
                        isError = errorMessage.isNotEmpty() && username.text.isEmpty()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
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
                                fontFamily = urbanistLight,
                                fontSize = 14.sp
                            )
                        },
                        isError = errorMessage.isNotEmpty() && email.text.isEmpty()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
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
                                fontFamily = urbanistLight,
                                fontSize = 14.sp
                            )
                        },
                        isError = errorMessage.isNotEmpty() && password.text.isEmpty()
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontFamily = urbanistLight
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    val customButtonColor = Color(0xFF492468)
                    Button(
                        onClick = {
                            if (username.text.isEmpty() || email.text.isEmpty() || password.text.isEmpty()) {
                                errorMessage = "Please fill in all fields."
                            } else {
                                errorMessage = ""
                                viewModel.signUp(username.text, email.text, password.text) {
                                    Toast.makeText(context, "Account successfully created!", Toast.LENGTH_SHORT).show()
                                    onNavigateBackToLogin()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = customButtonColor),
                        modifier = Modifier
                            .width(150.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(0.dp),
                    ) {
                        Text(text = "Sign Up", color = Color.White, fontSize = 17.sp, fontFamily = urbanistLight)
                    }
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
                        text = "Already have an account?",
                        fontFamily = urbanistLight,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val logInButtonColor = Color(0xFF492468)
                    Text(
                        text = "Log In",
                        fontFamily = urbanistLight,
                        fontSize = 14.sp,
                        color = logInButtonColor,
                        modifier = Modifier.clickable { onNavigateBackToLogin() }
                    )
                }
            }
        }
    )
}
