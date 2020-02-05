package com.nesoinode.notes.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nesoinode.notes.R

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteHolder>() {
    private var notes:List<Note> = ArrayList()
    private var listener: OnItemClickListener? = null

    inner class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle:TextView = itemView.findViewById(R.id.text_view_title)
        var textViewDescription:TextView = itemView.findViewById(R.id.text_view_description)
        var textViewPriority:TextView = itemView.findViewById(R.id.text_view_priority)
        // Set each NoteHolder's onClickListener to use the one specified using the OnItemClickListener interface
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if(listener != null &&  position != RecyclerView.NO_POSITION) { // NO_POSITION == -1
                    listener!!.onItemClick(notes[position])
                }
            }
        }
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
    fun setNotes(notes:List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    /**
     * Returns the Note object at the given position
     * @param position The position from which the object
     * will be retrieved
     */
    fun getNoteAt(position: Int) : Note {
        return notes[position]
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    interface OnItemClickListener {
        fun onItemClick(note:Note)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
