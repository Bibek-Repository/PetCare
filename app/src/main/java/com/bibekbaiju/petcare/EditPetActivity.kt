package com.bibekbaiju.petcare

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream

class EditPetActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var petImageView: ImageView
    private lateinit var petNameEditText: TextInputEditText
    private lateinit var speciesEditText: TextInputEditText
    private lateinit var breedEditText: TextInputEditText
    private lateinit var ageEditText: TextInputEditText
    private lateinit var notesEditText: TextInputEditText
    private lateinit var selectPetImageButton: MaterialButton
    private lateinit var updatePetButton: MaterialButton

    private var petId: String = ""
    private var currentImagePath: String = ""

    private var selectedImageUri: Uri? = null

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {
                selectedImageUri = uri
                petImageView.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_pet)

        db = FirebaseFirestore.getInstance()

        petImageView = findViewById(R.id.petImageView)

        petNameEditText =
            findViewById(R.id.petNameEditText)

        speciesEditText =
            findViewById(R.id.speciesEditText)

        breedEditText =
            findViewById(R.id.breedEditText)

        ageEditText =
            findViewById(R.id.ageEditText)

        notesEditText =
            findViewById(R.id.notesEditText)

        selectPetImageButton =
            findViewById(R.id.selectPetImageButton)

        updatePetButton =
            findViewById(R.id.updatePetButton)

        petId = intent.getStringExtra("petId") ?: ""

        if (petId.isEmpty()) {

            Toast.makeText(
                this,
                "Pet information not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        loadPet()

        selectPetImageButton.setOnClickListener {
            imagePicker.launch("image/*")
        }

        updatePetButton.setOnClickListener {
            updatePet()
        }
    }

    private fun loadPet() {

        db.collection("pets")
            .document(petId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    petNameEditText.setText(
                        document.getString("name") ?: ""
                    )

                    speciesEditText.setText(
                        document.getString("species") ?: ""
                    )

                    breedEditText.setText(
                        document.getString("breed") ?: ""
                    )

                    ageEditText.setText(
                        document.getString("age") ?: ""
                    )

                    notesEditText.setText(
                        document.getString("notes") ?: ""
                    )

                    currentImagePath =
                        document.getString("imagePath") ?: ""

                    // Display existing image
                    if (currentImagePath.isNotEmpty()) {

                        val imageFile = File(currentImagePath)

                        if (imageFile.exists()) {

                            petImageView.setImageURI(
                                Uri.fromFile(imageFile)
                            )
                        }
                    }

                } else {

                    Toast.makeText(
                        this,
                        "Pet not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load pet: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun updatePet() {

        val name =
            petNameEditText.text.toString().trim()

        val species =
            speciesEditText.text.toString().trim()

        val breed =
            breedEditText.text.toString().trim()

        val age =
            ageEditText.text.toString().trim()

        val notes =
            notesEditText.text.toString().trim()

        if (name.isEmpty()) {

            petNameEditText.error =
                "Please enter the pet's name"

            petNameEditText.requestFocus()

            return
        }

        if (species.isEmpty()) {

            speciesEditText.error =
                "Please enter the species"

            speciesEditText.requestFocus()

            return
        }

        updatePetButton.isEnabled = false
        updatePetButton.text = "Updating..."

        // If a new image was selected, copy it to app storage
        val newImagePath =
            if (selectedImageUri != null) {
                copyImageToInternalStorage()
            } else {
                currentImagePath
            }

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "species" to species,
            "breed" to breed,
            "age" to age,
            "notes" to notes,
            "imagePath" to newImagePath
        )

        db.collection("pets")
            .document(petId)
            .update(updates)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Pet updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener { exception ->

                updatePetButton.isEnabled = true
                updatePetButton.text = "Update Pet"

                Toast.makeText(
                    this,
                    "Failed to update pet: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun copyImageToInternalStorage(): String {

        val uri = selectedImageUri ?: return currentImagePath

        return try {

            val directory =
                File(filesDir, "pet_images")

            if (!directory.exists()) {
                directory.mkdirs()
            }

            val fileName =
                "pet_${System.currentTimeMillis()}.jpg"

            val destinationFile =
                File(directory, fileName)

            contentResolver
                .openInputStream(uri)
                ?.use { inputStream ->

                    FileOutputStream(destinationFile)
                        .use { outputStream ->

                            inputStream.copyTo(outputStream)
                        }
                }

            destinationFile.absolutePath

        } catch (exception: Exception) {

            Toast.makeText(
                this,
                "Could not save image: ${exception.message}",
                Toast.LENGTH_LONG
            ).show()

            currentImagePath
        }
    }
}