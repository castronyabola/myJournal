package com.example.bboxxjournal

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_add_notes.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_notes.view.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
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
                R.layout.item_notes, parent, false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNotes(note: Notes) {
        notes.add(note)
        notes.sortBy { LocalDate.parse(it.time, dateFormatter) }
        notifyItemInserted(notes.size - 1)

    }

    fun deleteNotes() {
        notes.removeAll { note ->
            note.isChecked
        }
        notifyItemRemoved(notes.size - 1)
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
            tvTime.text = dateTime.format(timeFormatter)
            cvMood.setCardBackgroundColor(curNote.mood)
            curNote.isChecked = cbDelete.isChecked
            cbDelete.setOnCheckedChangeListener { _, _ ->
                curNote.isChecked = !curNote.isChecked
            }
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
    override fun onBindViewHolder(
        holder: NotesViewHolder, position: Int, payloads: MutableList<Any>
    ) {
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
        val dateTime = LocalDateTime.parse(curNote.time, dateFormatter)
        val curDate = LocalDateTime.parse(curNote.time, dateFormatter)
        val prevDate = if (holder.adapterPosition > 0) {
            LocalDate.parse(notes[holder.adapterPosition - 1].time, dateFormatter)
        } else {
            null
        }

        if (prevDate == null || curDate.monthValue != prevDate.monthValue) {

            val filteredNotesGreen = notes.filter { note ->
                note.mood.toString().contains("-3080514")
            }
            val filteredNotesYellow = notes.filter { note ->
                note.mood.toString().contains("-66626")
            }
            val filteredNotesRed = notes.filter { note ->
                note.mood.toString().contains("-32397")
            }

            val filteredNotes = notes.filter { note ->
                LocalDate.parse(note.time, dateFormatter).month.getDisplayName(
                    TextStyle.FULL, Locale.ENGLISH
                ).toString().lowercase() == dateTime.toLocalDate().month.toString().lowercase()
            }

            holder.itemView.apply {
                val greenCount = filteredNotesGreen.size // number of green entries
                val yellowCount = filteredNotesYellow.size // number of yellow entries
                val redCount = filteredNotesRed.size // number of red entries

                val totalCount = greenCount + yellowCount + redCount
                val greenPercentage = greenCount.toFloat() / totalCount
                val yellowPercentage = yellowCount.toFloat() / totalCount
                val redPercentage = redCount.toFloat() / totalCount

                fun mixColors(
                    redPercentage: Float, yellowPercentage: Float, greenPercentage: Float
                ): Int {
                    val totalPercentage = redPercentage + yellowPercentage + greenPercentage
                    val red = Color.parseColor("#FF8173")
                    val yellow = Color.parseColor("#FEFBBE")
                    val green = Color.parseColor("#D0FEBE")

                    val mixedRed =
                        (Color.red(red) * redPercentage + Color.red(yellow) * yellowPercentage + Color.red(
                            green
                        ) * greenPercentage) / totalPercentage
                    val mixedGreen =
                        (Color.green(red) * redPercentage + Color.green(yellow) * yellowPercentage + Color.green(
                            green
                        ) * greenPercentage) / totalPercentage
                    val mixedYellow =
                        (Color.blue(red) * redPercentage + Color.blue(yellow) * yellowPercentage + Color.blue(
                            green
                        ) * greenPercentage) / totalPercentage

                    return Color.rgb(mixedRed.toInt(), mixedGreen.toInt(), mixedYellow.toInt())
                }

                val mixedColor = mixColors(redPercentage, yellowPercentage, greenPercentage)

                tvMonth.text = "%s".format(curDate.month.toString())

                if (filteredNotes.size == 1) {
                    tvMonthEntries.text = "%s entry".format(filteredNotes.size)
                } else {
                    tvMonthEntries.text = "%s entries".format(filteredNotes.size)
                }

                tvMonth.setBackgroundColor(mixedColor)
                tvMonthEntries.setBackgroundColor(mixedColor)
            }

        } else {
            holder.itemView.apply {
                tvMonth.visibility = View.GONE
                tvMonthEntries.visibility = View.GONE
            }
        }

        if (prevDate == null || curDate.dayOfMonth != prevDate.dayOfMonth) {

            val filteredNotes = notes.filter { note ->
                LocalDate.parse(note.time, dateFormatter) == dateTime.toLocalDate()
            }
            holder.itemView.apply {
                tvDate.text = "%s, %s".format(
                    dateTime.toLocalDate().dayOfWeek.toString().lowercase()
                        .replaceFirstChar { it.uppercase() },
                    dateTime.toLocalDate().dayOfMonth.toString()
                )
                if (filteredNotes.size == 1) {
                    tvDateEntries.text = "%s entry".format(filteredNotes.size)
                } else {
                    tvDateEntries.text = "%s entries".format(filteredNotes.size)
                }
            }
        } else {
            holder.itemView.apply {
                tvDate.visibility = View.GONE
                tvDateEntries.visibility = View.GONE
            }
        }
    }
}












