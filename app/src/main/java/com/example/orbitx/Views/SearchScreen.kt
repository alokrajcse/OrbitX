package com.example.orbitx.Views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
            .background(Color(0xFFF4F3F8)) // Soft purple/grey app theme background
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFFA0AEC0) // Soft grey for icon
                            )
                        },
                        value = searchText,
                        onValueChange = viewModel::onSearchTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(26.dp)) 
                            .background(Color(0xFFF4F3F8)), // Match app background in input
                        placeholder = {
                            Text(text = "Search users...", fontSize = 16.sp, color = Color(0xFFA0AEC0))
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedTextColor = Color(0xFF2D3748),
                            unfocusedTextColor = Color(0xFF2D3748),
                            containerColor = Color.Transparent, 
                            focusedIndicatorColor = Color.Transparent, 
                            unfocusedIndicatorColor = Color.Transparent 
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(persons) { person ->
                    var userprofilepicurl by remember { mutableStateOf("") }

                    LaunchedEffect(person.parent) {
                        fetchProfileurl(person.parent) { it ->
                            userprofilepicurl = it
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("otheruserprofile/${person.parent}")
                                println("UUUID:${person.parent}")
                            },
                        colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = userprofilepicurl.ifEmpty { R.drawable.avataricon },
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = person.userName,
                                style = LocalTextStyle.current.copy(
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF2D3748)
                                )
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