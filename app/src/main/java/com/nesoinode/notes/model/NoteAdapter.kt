package com.nesoinode.notes.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nesoinode.notes.R

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteHolder>() {
    private var notes:List<Note> = ArrayList()

    class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public var textViewTitle:TextView = itemView.findViewById(R.id.text_view_title)
        public var textViewDescription:TextView = itemView.findViewById(R.id.text_view_description)
        public var textViewPriority:TextView = itemView.findViewById(R.id.text_view_priority)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val itemView:View = LayoutInflater.from(parent.context).inflate(R.layout.note_item,parent,false)
        return NoteHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val currentNote:Note = notes[position]
        holder.textViewTitle.text = currentNote.title
        holder.textViewDescription.text = currentNote.description
        holder.textViewPriority.text = currentNote.priority.toString()
    }

    /**
     * Passes the new data to the adapter
     * and notifies the RecyclerView that the data has changed
     * @param notes A List of new Note data
     */
    public fun setNotes(notes:List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    /**
     * Returns the Note object at the given position
     * @param position The position from which the object
     * will be retrieved
     */
    public fun getNoteAt(position: Int) : Note {
        return notes[position]
    }

    override fun getItemCount(): Int {
        return notes.size
    }

}
