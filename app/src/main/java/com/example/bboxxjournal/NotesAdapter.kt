package com.example.bboxxjournal

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_add_notes.*
import kotlinx.android.synthetic.main.item_notes.view.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class NotesAdapter(
    private var notes: MutableList<Notes>

) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

    class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_notes,
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNotes(note: Notes) {
        notes.add(note)
        //notes = notes.sortedByDescending { LocalDate.parse(it.time, dateFormatter) }.toMutableList()
        notes.sortBy { LocalDate.parse(it.time, dateFormatter) }
        notifyDataSetChanged()

    }

    var notesList: List<Notes> = emptyList()
        get() = notes

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val curNote = notes[position]
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        holder.itemView.apply {
            tvNotes.text = curNote.title
            val dateTime = LocalDateTime.parse(curNote.time, dateTimeFormatter)
            //tvDate.text = "%s, %s".format(dateTime.toLocalDate().dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() }, dateTime.toLocalDate().dayOfMonth.toString())//"${dateTime.toLocalDate().dayOfWeek.toString()},  ${dateTime.toLocalDate().dayOfMonth.toString()}"
            tvTime.text = dateTime.format(timeFormatter)
            cvMood.setCardBackgroundColor(curNote.mood)
        }
    }


    override fun getItemCount(): Int {
        return notes.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getItemViewType(position: Int): Int {
        val curNote = notes[position]
        val curDate = LocalDateTime.parse(curNote.time, dateFormatter)
        val prevDate = if (position > 0) {
            LocalDateTime.parse(notes[position - 1].time, dateFormatter)
        } else {
            null
        }

        return if (prevDate == null || curDate.month != prevDate.month || curDate.dayOfMonth != prevDate.dayOfMonth) {
            0 // month header view
        } else {
            1 // normal note view
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val curNote = notes[position]
            holder.itemView.apply {
                cvMood.setCardBackgroundColor(curNote.mood)
            }
        }
    }

    override fun onViewRecycled(holder: NotesViewHolder) {
        super.onViewRecycled(holder)
        holder.itemView.apply {
            cvMood.setCardBackgroundColor(0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewAttachedToWindow(holder: NotesViewHolder) {
        super.onViewAttachedToWindow(holder)
        val curNote = notes[holder.adapterPosition]
        val curDate = LocalDateTime.parse(curNote.time, dateFormatter)
        val prevDate = if (holder.adapterPosition > 0) {
            LocalDate.parse(notes[holder.adapterPosition - 1].time, dateFormatter)
        } else {
            null
        }

        if (prevDate == null || curDate.monthValue != prevDate.monthValue) {

            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            val dateTime = LocalDateTime.parse(curNote.time, dateTimeFormatter)
            val filteredNotesGreen = notes.filter {
                    note ->
                note.mood.toString().contains("-16711936")
            }

            //println("##############: $notes")

            val filteredNotesYellow = notes.filter {
                    note ->
                note.mood.toString().contains("-256") //== ColorStateList.valueOf(Color.YELLOW)
            }
            val filteredNotesRed = notes.filter {
                    note ->
                note.mood.toString().contains("-65536")
            }
            val filteredNotes = notes.filter {
                    note ->
                note.time.contains(dateTime.toLocalDate().dayOfMonth.toString())
            }
            holder.itemView.apply {
                val greenCount = filteredNotesGreen.size // number of green entries
                val yellowCount = filteredNotesYellow.size // number of yellow entries
                val redCount = filteredNotesRed.size // number of red entries

                val totalCount = greenCount + yellowCount + redCount
                val greenPercentage = greenCount.toFloat() / totalCount
                val yellowPercentage = yellowCount.toFloat() / totalCount
                val redPercentage = redCount.toFloat() / totalCount

                val color = when {
                    greenPercentage > 0.5 -> Color.GREEN
                    redPercentage > 0.5 -> Color.RED
                    yellowPercentage > 0.5 -> Color.YELLOW
                    redPercentage.toDouble() == 0.5 && greenPercentage.toDouble() == 0.5 -> Color.YELLOW
                    yellowPercentage.toDouble() == 0.5 && greenPercentage.toDouble() == 0.5 -> Color.parseColor("#CEFF33")
                    redPercentage.toDouble() == 0.5 && yellowPercentage.toDouble() == 0.5 -> Color.parseColor("#FFA533")
                    else -> Color.YELLOW
                }


                tvMonth.text = "%s".format(curDate.month.toString())
                tvMonthEntries.text = "%s entries".format(filteredNotes.size)

                tvMonth.setBackgroundColor(color)
                tvMonthEntries.setBackgroundColor(color)

            }

        }

        if (prevDate == null || curDate.dayOfMonth != prevDate.dayOfMonth) {

            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            val dateTime = LocalDateTime.parse(curNote.time, dateTimeFormatter)

            val filteredNotes = notes.filter {
                    note ->
                note.time.contains(dateTime.toLocalDate().dayOfMonth.toString()) && note.time.contains(dateTime.toLocalDate().dayOfMonth.toString())
            }
            holder.itemView.apply {
                tvDate.text = "%s, %s".format(dateTime.toLocalDate().dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() }, dateTime.toLocalDate().dayOfMonth.toString())
                tvDateEntries.text = "%s entries".format(filteredNotes.size)
            }
        }else{
            holder.itemView.apply {
                tvDate.text = null
                tvDate.setBackgroundColor(Color.TRANSPARENT)
                tvDateEntries.text = null
                tvDateEntries.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}












