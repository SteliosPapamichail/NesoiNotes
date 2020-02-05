package com.nesoinode.notes.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nesoinode.notes.R
import com.nesoinode.notes.model.Note
import com.nesoinode.notes.model.NoteAdapter
import com.nesoinode.notes.viewmodel.NoteViewModel

class MainActivity() : AppCompatActivity() {
    private lateinit var noteViewModel: NoteViewModel

    companion object { // const values for activity request codes
        public const val ADD_NOTE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val floatingActionButton: FloatingActionButton = findViewById(R.id.addNoteFAB)
        floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = NoteAdapter()
        recyclerView.adapter = adapter

        // Specify the activity's viewModel class
        noteViewModel = ViewModelProviders.of(this)
            .get(NoteViewModel::class.java) // Bind the NoteViewModel class to the lifecycle of this activity
        // Observe the LiveData present in the NoteViewModel instance
        noteViewModel.getAllNotes()
            .observe(this, // set the lifecycle owner & the observer for the LiveData returned by the NoteViewModel class
                Observer<List<Note>> { notes ->
                    // Update the RecyclerView
                    adapter.setNotes(notes)
                })

        // Implement touch events on the RecyclerView such as swipe-to-delete etc.
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) { // dragDirs == 0 since we don't want to be able to drag & drop items
            // Used for handling drag & drop events (not gonna use it in this project)
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Used for handling swipe events (such as swipe to delete)
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.adapterPosition)) // remove the Note object that was swiped
                Toast.makeText(this@MainActivity,"Note deleted",Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(recyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If everything went right in the AddNoteActivity
        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Get the data from the Intent Extras
            val title = data!!.getStringExtra(AddNoteActivity.EXTRA_TITLE)
            val description = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION)
            val priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1)

            val note =
                Note(title!!, description!!, priority) // create the new Note object using the data
            noteViewModel.insert(note) // insert the object to the database
            Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show()
        } else { // e.g. when the user used the back button to leave the AddNoteActivity
            Toast.makeText(this, "Note not added", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the custom menu
        menuInflater.inflate(R.menu.delete_all_notes_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_all_notes -> {
                noteViewModel.deleteAllNotes()
                Toast.makeText(this,"All notes deleted successfully",Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
