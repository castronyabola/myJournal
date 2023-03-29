package com.example.bboxxjournal

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_notes.*
import kotlinx.android.synthetic.main.item_notes.view.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notesAdapter: NotesAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("notes", Context.MODE_PRIVATE)
        notesAdapter = NotesAdapter(loadNotes())

        rvNoteItems.adapter = notesAdapter
        rvNoteItems.layoutManager = LinearLayoutManager(this)

        fabNav.setOnClickListener {
            val intent = Intent(this, ActivityAddNotes::class.java)
            startActivity(intent)
        }

        fabDel.setOnClickListener {

            if(cbDelete.isChecked) {
                notesAdapter.deleteNotes()

                saveNotes()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadNotes(): MutableList<Notes> {

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

        val notesList = mutableListOf<Notes>()
        var sortedNotesList = mutableListOf<Notes>()

        val gson = Gson()
        val json = sharedPreferences.getString("notes", null)
        if (json != null) {
            val notesArray = gson.fromJson(json, Array<Notes>::class.java)
            notesList.addAll(notesArray)
            sortedNotesList = notesList.sortedByDescending { LocalDateTime.parse(it.time, dateFormatter) }.toMutableList()

        }

        progressBar.visibility = View.GONE
        if(sortedNotesList.isNotEmpty()){
            tvEmptyList.visibility = View.GONE
        }
        return sortedNotesList

    }

    private fun saveNotes() {
        val gson = Gson()

        val notesList = notesAdapter.notesList

        val editor = sharedPreferences.edit()
        val updatedNotesJson = gson.toJson(notesList)
        editor.putString("notes", updatedNotesJson)
        editor.apply()

        notesAdapter.notesList = notesList.toList()
        notesAdapter.notifyDataSetChanged()
    }
}
