package com.nesoinode.notes.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nesoinode.notes.R

class NoteAdapter :
    ListAdapter<Note, NoteAdapter.NoteHolder>(DIFF_CALLBACK) {

    // Defining the DIFF logic in this companion object that will be used by the super class to
    // handle differences between old and new data after changes have been made to the model
    companion object {
        @JvmStatic
        private val DIFF_CALLBACK : DiffUtil.ItemCallback<Note> =
            object:DiffUtil.ItemCallback<Note>() {
                override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                    return oldItem.title == newItem.title && oldItem.description == newItem.description
                            && oldItem.priority == newItem.priority && oldItem.dateAdded == newItem.dateAdded
                }
            }
    }

    private var listener: OnItemClickListener? = null

    inner class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle:TextView = itemView.findViewById(R.id.text_view_title)
        var textViewDescription:TextView = itemView.findViewById(R.id.text_view_description)
        var textViewPriority:TextView = itemView.findViewById(R.id.text_view_priority)
        var textViewDateAdded:TextView = itemView.findViewById(R.id.textView_date_added)
        // Set each NoteHolder's onClickListener to use the one specified using the OnItemClickListener interface
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if(listener != null &&  position != RecyclerView.NO_POSITION) { // NO_POSITION == -1
                    listener!!.onItemClick(getItem(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val itemView:View = LayoutInflater.from(parent.context).inflate(R.layout.note_item,parent,false)
        return NoteHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val currentNote:Note = getItem(position)
        holder.textViewTitle.text = currentNote.title
        holder.textViewDescription.text = currentNote.description
        holder.textViewPriority.text = currentNote.priority.toString()
        holder.textViewDateAdded.text = currentNote.dateAdded
    }

    /**
     * Returns the Note object at the given position
     * @param position The position from which the object
     * will be retrieved
     */
    fun getNoteAt(position: Int) : Note {
        return getItem(position)
    }

    interface OnItemClickListener {
        fun onItemClick(note:Note)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
