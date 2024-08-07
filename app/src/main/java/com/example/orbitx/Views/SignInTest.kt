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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.orbitx.R

@Composable
fun SignInTestScreen() {
    val customColor = Color(0xFF121212)
    val urbanistLight = FontFamily(Font(R.font.urbanist_light))
    val urbanistRegular = FontFamily(Font(R.font.urbanist_regular))
    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))
    val cardBackgroundColor = Color(0x99000000) // Semi-transparent white

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Background Image
                Image(
                    painter = painterResource(id = R.drawable.pic),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Center)
                ) {
                    // Centered Card with light shade of background
                    Card(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(20.dp)// Adjusts the size of the card to the content
                            .align(Alignment.Center), // Centers the card
                        shape = RoundedCornerShape(2.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "Sign In",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = urbanistRegular,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(50.dp))
                            var email by remember { mutableStateOf(TextFieldValue("")) }
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier
                                    .padding(horizontal = 0.dp)
                                    .fillMaxWidth(),
                                textStyle = TextStyle(
                                    color = Color.White,
                                    fontFamily = urbanistLight,
                                    fontSize = 14.sp
                                ),
                                placeholder = {
                                    Text(
                                        text = "Email address",
                                        color = Color.White,
                                        fontFamily = urbanistLight,
                                        fontSize = 14.sp
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            var password by remember { mutableStateOf(TextFieldValue("")) }
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier
                                    .padding(horizontal = 0.dp)
                                    .fillMaxWidth(),
                                textStyle = TextStyle(
                                    color = Color.White,
                                    fontFamily = urbanistLight,
                                    fontSize = 14.sp
                                ),
                                placeholder = {
                                    Text(
                                        text = "Password",
                                        color = Color.White,
                                        fontFamily = urbanistLight,
                                        fontSize = 14.sp
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                            val customButtonColor = Color(0xFF492468)
                            val customClr = Color(0xFF6650a4)

                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = customClr),
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(0.dp),
                            ) {
                                Text(
                                    text = "Log In",
                                    color = Color.White,
                                    fontFamily = urbanistMedium,
                                    fontSize = 17.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Forgot Password?",
                                fontFamily = urbanistLight,
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable { /* Handle Forgot Password */ }
                                    .padding(vertical = 8.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
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
                            fontFamily = urbanistLight,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val customButtonColor = Color(0xFFD0BCFF)
                        Text(
                            text = "Create one",
                            fontFamily = urbanistLight,
                            fontSize = 14.sp,
                            color = customButtonColor,
                            modifier = Modifier.clickable { }
                        )
                    }
                }
            }
        }
    )
}
