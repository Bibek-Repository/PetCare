package com.bibekbaiju.petcare

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddTaskActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var petSpinner: Spinner

    private val pets = mutableListOf<Pet>()
    private val petNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val taskTitleEditText =
            findViewById<TextInputEditText>(R.id.taskTitleEditText)

        petSpinner =
            findViewById(R.id.petSpinner)

        val frequencySpinner =
            findViewById<Spinner>(R.id.frequencySpinner)

        val timeEditText =
            findViewById<TextInputEditText>(R.id.timeEditText)

        val suppliesEditText =
            findViewById<TextInputEditText>(R.id.suppliesEditText)

        val notesEditText =
            findViewById<TextInputEditText>(R.id.notesEditText)

        val saveTaskButton =
            findViewById<MaterialButton>(R.id.saveTaskButton)

        // Frequency options
        val frequencies = listOf(
            "Daily",
            "Weekly"
        )

        val frequencyAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            frequencies
        )

        frequencyAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        frequencySpinner.adapter = frequencyAdapter

        // Load pets from Firestore
        loadPets()

        saveTaskButton.setOnClickListener {

            val taskTitle =
                taskTitleEditText.text.toString().trim()

            val time =
                timeEditText.text.toString().trim()

            val supplies =
                suppliesEditText.text.toString().trim()

            val notes =
                notesEditText.text.toString().trim()

            if (taskTitle.isEmpty()) {
                taskTitleEditText.error = "Please enter a task name"
                taskTitleEditText.requestFocus()
                return@setOnClickListener
            }

            if (pets.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please add a pet first",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val selectedPetIndex =
                petSpinner.selectedItemPosition

            val selectedPet =
                pets[selectedPetIndex]

            val frequency =
                frequencySpinner.selectedItem.toString()

            saveTaskButton.isEnabled = false
            saveTaskButton.text = "Saving..."

            val currentUser = auth.currentUser

            if (currentUser == null) {
                Toast.makeText(
                    this,
                    "Please log in first",
                    Toast.LENGTH_SHORT
                ).show()

                saveTaskButton.isEnabled = true
                saveTaskButton.text = "Save Routine"
                return@setOnClickListener
            }

            val task = hashMapOf(
                "petId" to selectedPet.id,
                "petName" to selectedPet.name,
                "title" to taskTitle,
                "frequency" to frequency,
                "time" to time,
                "supplies" to supplies,
                "notes" to notes,
                "completed" to false,
                "userId" to currentUser.uid,
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("tasks")
                .add(task)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Care routine saved!",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
                .addOnFailureListener { exception ->

                    saveTaskButton.isEnabled = true
                    saveTaskButton.text = "Save Routine"

                    Toast.makeText(
                        this,
                        "Failed to save routine: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun loadPets() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                "Please log in first",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        db.collection("pets")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->

                pets.clear()
                petNames.clear()

                for (document in result) {

                    val pet =
                        document.toObject(Pet::class.java)

                    pet.id = document.id

                    pets.add(pet)
                    petNames.add(pet.name)
                }

                val petAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    petNames
                )

                petAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                petSpinner.adapter = petAdapter
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load pets: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}