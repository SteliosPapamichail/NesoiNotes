package com.nesoinode.notes.model

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(entities = [Note::class],version = 2)
public abstract class NoteDatabase : RoomDatabase() {

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
                        .addCallback(roomCallback)
                        .build()
                }
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }

        private val roomCallback = object : RoomDatabase.Callback() {
            // runs the first time the database is created
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(instance) // Populate the db the first time it's created with some "fake" data
                    .execute()
            }
        }
    }

    class PopulateDbAsyncTask(db: NoteDatabase?) : AsyncTask<Void, Void, Void>() {
        private val noteDao = db?.noteDao()

        override fun doInBackground(vararg p0: Void?): Void? {
            noteDao?.insert(Note("title 1", "description 1", 1))
            noteDao?.insert(Note("title 2", "description 2", 2))
            noteDao?.insert(Note("title 3", "description 3", 3))
            return null
        }
    }

    // Provides access to the database operations for the Note entity
    abstract fun noteDao() : NoteDao
}