package com.example.orbitx.Views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orbitx.MainViewModel

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()
    val searchText by viewModel.searchText.collectAsState()
    val persons by viewModel.persons.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Search") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(persons) { person ->
                Text(
                    text = "${person.userName} ${person.email}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
        }
    }
}
