package com.example.orbitx.Views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.orbitx.ChatRepository.fetchProfileurl
import com.example.orbitx.MainViewModel
import com.example.orbitx.R // Import your resources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()
    val searchText by viewModel.searchText.collectAsState()
    val persons by viewModel.persons.collectAsState()

    var profilepicurl by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .background(Color(0xFFFFE0B2))
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.background(color = colorResource(id = R.color.orange)).padding(5.dp), verticalAlignment = Alignment.CenterVertically) {

//                IconButton(onClick = {
//                    navController.navigateUp()
//                }) {
//                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
//
//                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        value = searchText,
                        onValueChange = viewModel::onSearchTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)  // Adjust the height to match Instagram's style
                            .clip(CircleShape) // Rounded corners
                            .background(Color(0xFFEFEFEF)), // Light grey background
                        placeholder = {
                            Text(text = "Search", fontSize = 16.sp) // Adjust font size here
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedTextColor =  Color.Black,
                            containerColor = Color.Transparent, // Transparent to use background
                            focusedIndicatorColor = Color.Transparent, // Remove underline
                            unfocusedIndicatorColor = Color.Transparent // Remove underline
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp) // Adjust font size here
                    )
                }


            }


            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(persons) { person ->
                    var profilepicurl by remember { mutableStateOf("") }

                    LaunchedEffect(person.parent) {
                        fetchProfileurl(person.parent) { it ->
                            profilepicurl = it
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate("otheruserprofile/${person.parent}")
                                println("UUUID:${person.parent}")
                            },
                        colors = CardDefaults.cardColors(Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = profilepicurl.ifEmpty { R.drawable.avataricon },
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .height(60.dp)
                                    .width(60.dp)
                            )

                            Text(
                                text = person.userName,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun searchscreenprev() {

    SearchScreen(navController = NavController(LocalContext.current))


}