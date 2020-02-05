package com.nesoinode.notes.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import com.nesoinode.notes.R
import kotlinx.android.synthetic.main.activity_add_note.*

class AddEditNoteActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priorityPicker: NumberPicker

    companion object { // static key constants for intent extras
        public const val EXTRA_TITLE = "com.nesoinode.notes.EXTRA_TITLE"
        public const val EXTRA_DESCRIPTION = "com.nesoinode.notes.EXTRA_DESCRIPTION"
        public const val EXTRA_PRIORITY = "com.nesoinode.notes.EXTRA_PRIORITY"
        public const val EXTRA_ID = "com.nesoinode.notes.EXTRA_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        // Initialize the views
        titleEditText = findViewById(R.id.edit_text_title)
        descriptionEditText = findViewById(R.id.edit_text_description)
        priorityPicker = findViewById(R.id.number_picker_priority)

        // Set the number picker's range
        priorityPicker.minValue = 1
        priorityPicker.maxValue = 10

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close) // set the drawable to show next to the action bar's title
        val intent = intent // retrieve the intent
        // set the action bar's title based on the EXTRA_ID extra
        if(intent.hasExtra(EXTRA_ID)) { // User wants to update the note details
            title = "Edit Note"
            // Fill the views with the data from the intent
            edit_text_title.setText(intent.getStringExtra(EXTRA_TITLE))
            edit_text_description.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
            priorityPicker.value = intent.getIntExtra(EXTRA_PRIORITY,1)
        } else { // user wants to add a new note
            title = "Add Note"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_note_menu,menu) // inflate the custom menu
        return true // show the options menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save_note -> {
                saveNote()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote() {
        // Retrieve the data from the input fields
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val priority = priorityPicker.value

        // Check if the input is valid
        if(title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this,"Please insert a title & description",Toast.LENGTH_SHORT).show()
            return
        }

        // Create a new intent and put the data in it
        val data = Intent()
        data.putExtra(EXTRA_TITLE,title)
        data.putExtra(EXTRA_DESCRIPTION,description)
        data.putExtra(EXTRA_PRIORITY,priority)

        val id = intent.getIntExtra(EXTRA_ID,-1) // retrieve the id extra (if there is one)
        if(id != -1) { // if it's not -1 then we've specified an id value explicitly and we want to put it as an extra
            data.putExtra(EXTRA_ID,id)
        }

        /*
           If we've reached this point we set the result code for the activity to ok.
           Otherwise, if the user used the back button for example, then we didn't save the note so the result code won't be ok.
         */
        setResult(Activity.RESULT_OK,data)
        finish()
    }
}
