package com.example.bboxxjournal

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_notes.*
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import android.content.SharedPreferences
import com.google.gson.reflect.TypeToken

class ActivityAddNotes : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var moodValue: ColorStateList

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("notes", Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        notesAdapter = NotesAdapter(mutableListOf())

        btnAddNote.setOnClickListener {
            val todoTitle = etNotes.text.toString()
            if(todoTitle.isNotEmpty()) {
                    tvPrompt.visibility = View.INVISIBLE
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    val currentTime = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                    val formattedTime = currentTime.format(formatter)
                    val note = Notes(todoTitle, formattedTime, moodValue)
                    notesAdapter.addNotes(note)

                    etNotes.text.clear()

                    val notesList = notesAdapter.notesList

                    saveNotes(notesList)

            }else{
                tvPrompt.visibility = View.VISIBLE
            }
        }


        btGreen.setOnClickListener {
            moodValue = ColorStateList.valueOf(Color.parseColor("#D0FEBE"))
            etNotes.visibility = View.VISIBLE
            tvQuest.text = "You Selected Happy, Meaning You're Happy!, Great!"
            tvQuest.setTextColor(Color.parseColor("#59C704"))
        }

        btYellow.setOnClickListener {
            moodValue = ColorStateList.valueOf(Color.parseColor("#FEFBBE"))
            etNotes.visibility = View.VISIBLE
            tvQuest.text = "You Selected Normal, Not Bad."
            tvQuest.setTextColor(Color.parseColor("#E8C800"))

        }
        btRed.setOnClickListener {
            moodValue = ColorStateList.valueOf(Color.parseColor("#FF8173"))
            etNotes.visibility = View.VISIBLE
            tvQuest.text = "You Selected Angry, Whats Up? Try and calm down buddy."
            tvQuest.setTextColor(Color.parseColor("#FF8173"))

        }
    }

    private fun saveNotes(notes: List<Notes>) {
        val gson = Gson()
        val notesJson = sharedPreferences.getString("notes", null)
        var notesList = gson.fromJson<List<Notes>>(notesJson, object : TypeToken<List<Notes>>() {}.type) ?: mutableListOf()

        notesList += notes

        val editor = sharedPreferences.edit()
        val updatedNotesJson = gson.toJson(notesList)
        editor.putString("notes", updatedNotesJson)
        editor.apply()

        notesAdapter.notesList = notesList.toList()
        notesAdapter.notifyDataSetChanged()
    }

}