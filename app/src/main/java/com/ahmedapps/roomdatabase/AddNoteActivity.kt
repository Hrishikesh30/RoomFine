package com.ahmedapps.roomdatabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.ahmedapps.roomdatabase.data.Note
import com.ahmedapps.roomdatabase.data.NoteDao
import com.ahmedapps.roomdatabase.data.NotesDatabase
import kotlinx.coroutines.launch

class AddNoteActivity : ComponentActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            NotesDatabase::class.java,
            "notes1.db"
        ).build()
    }

    private val noteDao: NoteDao by lazy {
        database.dao
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AddNoteScreen(noteDao = noteDao)
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(noteDao: NoteDao) {
    val titleState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope() // Remember the coroutine scope

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Add New Note", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = titleState.value,
            onValueChange = { titleState.value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Title") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = descriptionState.value,
            onValueChange = { descriptionState.value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Description") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val title = titleState.value
            val description = descriptionState.value
            if (title.isNotEmpty() && description.isNotEmpty()) {
                val note = Note(
                    title = title,
                    description = description
                )
                // Call upsertNote from within a coroutine scope
                coroutineScope.launch {
                    // Launch a coroutine in the scope
                    noteDao.upsertNote(note)
                }
            }
        }) {
            Text("Add Note")
        }
    }
}

