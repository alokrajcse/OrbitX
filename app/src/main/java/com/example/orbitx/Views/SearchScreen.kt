package com.example.orbitx.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orbitx.MainViewModel
import com.example.orbitx.R // Import your resources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()
    val searchText by viewModel.searchText.collectAsState()
    val persons by viewModel.persons.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Orange colored search bar
        TextField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange,
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(text = "Search") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xE3735E)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(persons) { person ->
                // Each search result in a Card with an Image and onClick event
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            // Handle onClick event here
                            println("Clicked on: ${person.userName}")
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Image in the card
                        val image: Painter = painterResource(id = R.drawable.avataricon) // Replace with your image resource
                        Image(
                            painter = image,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(64.dp)
                                .padding(end = 16.dp)
                        )

                        // Text in the card
                        Text(
                            text = person.userName, fontFamily = FontFamily.Monospace,
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
