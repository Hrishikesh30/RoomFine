package com.ahmedapps.roomdatabase

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.Room
import com.ahmedapps.roomdatabase.data.Note
import com.ahmedapps.roomdatabase.data.NoteDao
import com.ahmedapps.roomdatabase.data.NotesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

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

                    // DisplayNotes occupies the top half of the screen
                    DisplayNotes(noteDao)

                    AddNoteFloatingActionButton()

                }
            }
        }


    }
}

@Composable
fun DisplayNotes(noteDao: NoteDao) {
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }

        LaunchedEffect(Unit) {
        GlobalScope.launch(Dispatchers.IO) {

            noteDao.getNotesOrderdByTitle()
                .flowOn(Dispatchers.IO)
                .collect { fetchedNotes ->
                    notes = fetchedNotes
                }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notes) { note ->
            NoteItem(note)
        }
    }

}

@Composable
fun NoteItem(note: Note) {
    Text(text = "Title: ${note.title}, Description: ${note.description}")
}
@Composable
fun AddNoteFloatingActionButton() {
    val context = LocalContext.current

    SmallFloatingActionButton(
        onClick = {
            // Launch the activity for adding a new note
            val intent = Intent(context, AddNoteActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .padding(16.dp)
            .wrapContentSize(align = Alignment.BottomEnd)

    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
    }
}