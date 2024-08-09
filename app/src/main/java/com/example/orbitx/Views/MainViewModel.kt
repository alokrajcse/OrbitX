package com.example.orbitx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _persons = MutableStateFlow<List<Person>>(emptyList())
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

    init {
        fetchPersonsFromFirebase()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private fun fetchPersonsFromFirebase() {
        viewModelScope.launch {
            val userRef = FirebaseDatabase.getInstance().reference.child("users")

            userRef.get().addOnSuccessListener { dataSnapshot ->
                val personsList = mutableListOf<Person>()
                for (child in dataSnapshot.children) {
                    val username = child.child("username").getValue(String::class.java) ?: ""
                    personsList.add(Person(userName = username))
                }
                _persons.value = personsList
            }
        }
    }
}

data class Person(
    val userName: String
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            "$userName",
            "${userName.first()}"
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}
