package com.nesoinode.notes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    var title: String,
    var description: String,
    var priority: Int
) {
    // Automatically generate a unique incremental key for each instance of this entity
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}