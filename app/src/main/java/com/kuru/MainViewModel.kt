package com.kuru

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainViewModel(application: Application) : AndroidViewModel(application) {


    // 使用单例获取 Dao，避免重复建库
    private val noteDao = AppDatabase.getDatabase(application).noteDao()


    val notesFlow: StateFlow<List<NoteEntity>> = noteDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertNote(noteContent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedString = LocalDateTime.now().format(formatter)

            val note = NoteEntity(
                content = noteContent,
                date = formattedString
            )

            noteDao.insertAll(note)
        }
    }
}