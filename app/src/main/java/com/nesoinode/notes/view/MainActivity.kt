package com.nesoinode.notes.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nesoinode.notes.R
import com.nesoinode.notes.model.Note
import com.nesoinode.notes.model.NoteAdapter
import com.nesoinode.notes.viewmodel.NoteViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var noteViewModel: NoteViewModel

    companion object { // const values for activity request codes
        const val ADD_NOTE_REQUEST = 1
        const val EDIT_NOTE_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val floatingActionButton: FloatingActionButton = findViewById(R.id.addNoteFAB)
        floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }

        val noDataImgView:ImageView = findViewById(R.id.noDataImageView)
        val noDataTxtView:TextView = findViewById(R.id.noDataTextView)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = NoteAdapter()
        recyclerView.adapter = adapter

        // Specify the activity's viewModel class
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java) // Bind the NoteViewModel class to the lifecycle of this activity
        // Observe the LiveData present in the NoteViewModel instance
        noteViewModel.getAllNotes()
            .observe(this, // set the lifecycle owner & the observer for the LiveData returned by the NoteViewModel class
                Observer<List<Note>> { notes ->
                    if(notes.isEmpty() && !noDataImgView.isVisible && !noDataTxtView.isVisible) {
                        // Show an appropriate message
                        noDataImgView.visibility = View.VISIBLE
                        noDataTxtView.visibility = View.VISIBLE
                    } else {
                        // disable the message
                        noDataImgView.visibility = View.GONE
                        noDataTxtView.visibility = View.GONE
                    }
                    // Update the RecyclerView
                    adapter.submitList(notes)
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

        // Implement the adapter's onItemClickListener interface
        adapter.setOnItemClickListener(object:NoteAdapter.OnItemClickListener {
            override fun onItemClick(note: Note) {
                val intent = Intent(this@MainActivity,AddEditNoteActivity::class.java)
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE,note.title)
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION,note.description)
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY,note.priority)
                intent.putExtra(AddEditNoteActivity.EXTRA_ID,note.id)
                startActivityForResult(intent, EDIT_NOTE_REQUEST)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result code was OK and the user requested to Add a new Note
        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Get the data from the Intent Extras
            val title = data!!.getStringExtra(AddEditNoteActivity.EXTRA_TITLE)
            val description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION)
            val priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1)
            val dateAdded = data.getStringExtra(AddEditNoteActivity.EXTRA_DATE_ADDED)

            val note = Note(title!!, description!!, priority,dateAdded!!) // create the new Note object using the data
            noteViewModel.insert(note) // insert the object to the database
            Toast.makeText(this, "Note added successfully", Toast.LENGTH_SHORT).show()
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) { // result code was OK and the user decided to UPDATE a Note object
            val noteId = data!!.getIntExtra(AddEditNoteActivity.EXTRA_ID,-1) // retrieve the note's ID

            if(noteId == -1) { // something went wrong
                Toast.makeText(this,"Note couldn't be updated",Toast.LENGTH_SHORT).show()
                return
            }

            // Get the data from the Intent Extras
            val title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE)
            val description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION)
            val priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1)
            val dateAdded = data.getStringExtra(AddEditNoteActivity.EXTRA_DATE_ADDED)

            val note = Note(title!!,description!!,priority,dateAdded!!)
            note.id = noteId // set the note's primary key (ID) so that Room can identify which note to update
            noteViewModel.update(note) // update the note object with the noteId

            Toast.makeText(this,"Note updated",Toast.LENGTH_SHORT).show()
        } else { // Something went wrong e.g. when the user used the back button to leave the AddEditNoteActivity
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
