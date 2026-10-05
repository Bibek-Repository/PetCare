package com.bibekbaiju.petcare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PetsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var adapter: PetAdapter

    private val pets = mutableListOf<Pet>()

    override fun onResume() {
        super.onResume()
        loadPets()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.petsRecyclerView)
        emptyTextView = findViewById(R.id.emptyTextView)

        val addPetButton =
            findViewById<MaterialButton>(R.id.addPetButton)

        // Set up RecyclerView
        adapter = PetAdapter(
            pets,
            onEditClick = { pet ->
                val intent = Intent(this, EditPetActivity::class.java)
                intent.putExtra("petId", pet.id)
                startActivity(intent)
            },
            onDeleteClick = { pet ->
                deletePet(pet)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Add Pet button
        addPetButton.setOnClickListener {
            startActivity(Intent(this, AddPetActivity::class.java))
        }

        loadPets()

    }

    private fun deletePet(pet: Pet) {

        db.collection("pets")
            .document(pet.id)
            .delete()
            .addOnSuccessListener {

                pets.remove(pet)

                adapter.notifyDataSetChanged()

                if (pets.isEmpty()) {
                    emptyTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }

                Toast.makeText(
                    this,
                    "${pet.name} deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to delete pet: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
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

                for (document in result) {

                    val pet = document.toObject(Pet::class.java)

                    pet.id = document.id

                    pets.add(pet)
                }

                adapter.notifyDataSetChanged()

                if (pets.isEmpty()) {
                    emptyTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
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