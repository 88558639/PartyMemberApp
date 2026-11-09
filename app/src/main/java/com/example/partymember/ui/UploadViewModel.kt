package com.example.partymember.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.partymember.data.AppDatabase
import com.example.partymember.utils.ExcelParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val count: Int) : UploadState()
    data class Error(val message: String) : UploadState()
}

class UploadViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val parser = ExcelParser(application)

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    fun parseAndImport(inputStream: InputStream) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            try {
                val result = withContext(Dispatchers.IO) {
                    parser.parseExcel(inputStream)
                }

                result.fold(
                    onSuccess = { members ->
                        withContext(Dispatchers.IO) {
                            database.partyMemberDao().deleteAll()
                            database.partyMemberDao().insertAll(members)
                        }
                        _uploadState.value = UploadState.Success(members.size)
                    },
                    onFailure = { error ->
                        _uploadState.value = UploadState.Error(error.message ?: "解析失败")
                    }
                )
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "导入失败")
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    database.partyMemberDao().deleteAll()
                }
                _uploadState.value = UploadState.Success(0)
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "清空失败")
            }
        }
    }
}
