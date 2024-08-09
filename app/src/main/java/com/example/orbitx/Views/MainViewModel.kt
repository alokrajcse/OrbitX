package com.example.orbitx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _persons = MutableStateFlow(allPersons)
    val persons = searchText
        .combine(_persons) { text, persons ->
            if (text.isBlank()) {
                persons
            } else {
                persons.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(2000),
            _persons.value
        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}

data class Person(
    val userName: String,
    val email: String
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$userName$email",
            "$userName $email",
            "${userName.first()}${email.first()}",
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

private val allPersons = listOf(
    Person(
        userName = "nigger12345",
        email = "ohio@gmail.com"
    ),
    Person(
        userName = "karan68u6",
        email = "kuldeep@gmail.com"
    ),
    Person(
        userName = "67luffy",
        email = "luffy@gmail.com"
    )
)
