package com.nesoinode.notes.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
public interface NoteDao {

    @Insert
    fun insert(note:Note)

    @Update
    fun update(note:Note)

    @Delete
    fun delete(note:Note)

    @Query("DELETE FROM note_table")
    fun deleteAllNotes()

    @Query("SELECT * FROM note_table ORDER BY priority DESC")
    fun getAllNotes() : LiveData<List<Note>> // return LiveData so that we can observe for any changes
}