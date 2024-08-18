package com.example.orbitx.Views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.orbitx.MainViewModel
import com.example.orbitx.R // Import your resources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController,modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()
    val searchText by viewModel.searchText.collectAsState()
    val persons by viewModel.persons.collectAsState()


    Box(
        modifier = Modifier
            .background(Color(0xFFFFE0B2))
            .fillMaxSize()
    ) {

        Column(
            modifier = modifier
                .fillMaxSize()

        )

        {

            Box(modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.orange))
                .padding(16.dp)){

                Card(colors = CardDefaults.cardColors(Color.White)) {
                    TextField(
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
                        value = searchText,
                        onValueChange = viewModel::onSearchTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        placeholder = { Text(text = "Search") },
                        colors = OutlinedTextFieldDefaults.colors(Color.Black),
                        shape = TextFieldDefaults.shape,




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

                            val image: Painter = painterResource(id = R.drawable.avataricon)
                            Image(
                                painter = image,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(end = 16.dp)
                            )


                            Text(
                                text = person.userName,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
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