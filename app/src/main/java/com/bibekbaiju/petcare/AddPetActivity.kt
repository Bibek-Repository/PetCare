package com.bibekbaiju.petcare

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddPetActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pet)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val petNameEditText =
            findViewById<TextInputEditText>(R.id.petNameEditText)

        val speciesEditText =
            findViewById<TextInputEditText>(R.id.speciesEditText)

        val breedEditText =
            findViewById<TextInputEditText>(R.id.breedEditText)

        val ageEditText =
            findViewById<TextInputEditText>(R.id.ageEditText)

        val notesEditText =
            findViewById<TextInputEditText>(R.id.notesEditText)

        val savePetButton =
            findViewById<MaterialButton>(R.id.savePetButton)

        savePetButton.setOnClickListener {

            val petName = petNameEditText.text.toString().trim()
            val species = speciesEditText.text.toString().trim()
            val breed = breedEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim()
            val notes = notesEditText.text.toString().trim()

            if (petName.isEmpty()) {
                petNameEditText.error = "Please enter your pet's name"
                petNameEditText.requestFocus()
                return@setOnClickListener
            }

            if (species.isEmpty()) {
                speciesEditText.error = "Please enter the species"
                speciesEditText.requestFocus()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser

            if (currentUser == null) {
                Toast.makeText(
                    this,
                    "Please log in first",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            savePetButton.isEnabled = false
            savePetButton.text = "Saving..."

            val pet = hashMapOf(
                "name" to petName,
                "species" to species,
                "breed" to breed,
                "age" to age,
                "notes" to notes,
                "userId" to currentUser.uid,
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("pets")
                .add(pet)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Pet saved successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
                .addOnFailureListener { exception ->

                    savePetButton.isEnabled = true
                    savePetButton.text = "Save Pet"

                    Toast.makeText(
                        this,
                        "Failed to save pet: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}