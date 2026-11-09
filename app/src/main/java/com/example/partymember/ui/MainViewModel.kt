package com.example.partymember.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.partymember.data.AppDatabase
import com.example.partymember.data.PartyMember
import com.example.partymember.data.PartyMemberRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PartyMemberRepository(
        AppDatabase.getDatabase(application).partyMemberDao()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _members = MutableStateFlow<List<PartyMember>>(emptyList())
    val members: StateFlow<List<PartyMember>> = _members.asStateFlow()

    private val _hasData = MutableStateFlow(false)
    val hasData: StateFlow<Boolean> = _hasData.asStateFlow()

    init {
        refreshData()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchMembers(query)
    }

    private var searchJob: Job? = null

    private fun searchMembers(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isEmpty()) {
                _members.value = emptyList()
            } else {
                repository.searchMembers(query).collect { members ->
                    _members.value = members
                }
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            val count = repository.getCount()
            _hasData.value = count > 0
        }
    }

    private fun checkData() {
        viewModelScope.launch {
            val count = repository.getCount()
            _hasData.value = count > 0
        }
    }
}
