package com.nesoinode.notes.model

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class NoteRepository(application: Application) {
    private var noteDao:NoteDao
    private var allNotes:LiveData<List<Note>>

    init {
        val database = NoteDatabase.getInstance(application.applicationContext) // Create the database instance
        noteDao = database!!.noteDao()
        allNotes = noteDao.getAllNotes() // the observable live data
    }

    /*
        Room doesn't allow database operations to run on the main thread so we
        need to run those asynchronously. The following methods
        are the API that the repository class exposes to the rest of the app.
     */

    fun insert(note:Note) {
        InsertNoteAsync(noteDao).execute(note)
    }

    fun update(note: Note) {
        UpdateNoteAsync(noteDao).execute(note)
    }

    fun delete(note: Note) {
        DeleteNoteAsync(noteDao).execute(note)
    }

    fun deleteAllNotes() {
        DeleteAllNotesAsync(noteDao).execute()
    }

    fun getAllNotes() : LiveData<List<Note>> {return allNotes}

    companion object {
        private class InsertNoteAsync(private val noteDao: NoteDao) : AsyncTask<Note,Void,Void>() {

            override fun doInBackground(vararg p0: Note?): Void? {
                noteDao.insert(p0[0]!!)
                return null
            }

        }

        private class UpdateNoteAsync(private val noteDao: NoteDao) : AsyncTask<Note,Void,Void>() {

            override fun doInBackground(vararg p0: Note?): Void? {
                noteDao.update(p0[0]!!)
                return null
            }

        }

        private class DeleteNoteAsync(private val noteDao: NoteDao) : AsyncTask<Note,Void,Void>() {

            override fun doInBackground(vararg p0: Note?): Void? {
                noteDao.delete(p0[0]!!)
                return null
            }

        }

        private class DeleteAllNotesAsync(private val noteDao: NoteDao) : AsyncTask<Void,Void,Void>() {

            override fun doInBackground(vararg params: Void?): Void? {
                noteDao.deleteAllNotes()
                return null
            }

        }
    }

}