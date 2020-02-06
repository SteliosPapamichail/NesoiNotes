package com.nesoinode.notes.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class],version = 3)
abstract class NoteDatabase : RoomDatabase() {

    companion object {
        private var instance:NoteDatabase? = null

        fun getInstance(context: Context) : NoteDatabase? {
            if(instance == null) {
                synchronized(NoteDatabase::class) { // can run only on one thread at a time (singleton)
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NoteDatabase::class.java, "note_database"
                    )
                        .fallbackToDestructiveMigration() // when version increments, it migrates (deletes db and creates new) - else it crashes
                        .build()
                }
            }
            return instance
        }

    }
    // Provides access to the database operations for the Note entity
    abstract fun noteDao() : NoteDao
}