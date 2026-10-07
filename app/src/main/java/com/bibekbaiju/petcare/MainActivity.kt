package com.bibekbaiju.petcare

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var homePetsRecyclerView: RecyclerView
    private lateinit var noHomePetsTextView: TextView

    private lateinit var homeTasksRecyclerView: RecyclerView
    private lateinit var noHomeTasksTextView: TextView

    private val homePets = mutableListOf<Pet>()
    private val homeTasks = mutableListOf<CareTask>()

    private lateinit var homePetAdapter: HomePetAdapter

    private lateinit var homeTaskAdapter: HomeTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupBottomNavigation()
        setupPetRecyclerView()
    }

    override fun onResume() {
        super.onResume()

        loadPets()
        loadTasks()
        homeTasks.clear()
    }

    private fun setupPetRecyclerView() {

        homePetsRecyclerView = findViewById(R.id.homePetsRecyclerView)
        noHomePetsTextView = findViewById(R.id.noHomePetsTextView)

        homeTasksRecyclerView = findViewById(R.id.homeTasksRecyclerView)
        noHomeTasksTextView = findViewById(R.id.noHomeTasksTextView)

        homePetAdapter = HomePetAdapter(
            homePets
        ) {
            startActivity(Intent(this, PetsActivity::class.java))
        }

        homePetsRecyclerView.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        homePetsRecyclerView.adapter = homePetAdapter


        homeTaskAdapter = HomeTaskAdapter(
            homeTasks
        ) { task ->

            val intent = Intent(this, EditTaskActivity::class.java)

            intent.putExtra("taskId", task.id)

            startActivity(intent)
        }

        homeTasksRecyclerView.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        homeTasksRecyclerView.adapter = homeTaskAdapter
    }

    private fun loadPets() {

        val currentUser = auth.currentUser ?: return

        db.collection("pets")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->

                homePets.clear()

                for (document in result) {

                    val pet = document.toObject(Pet::class.java)

                    pet.id = document.id

                    homePets.add(pet)
                }

                homeTaskAdapter.notifyDataSetChanged()

                homePetAdapter.notifyDataSetChanged()

                if (homePets.isEmpty()) {

                    homePetsRecyclerView.visibility = android.view.View.GONE
                    noHomePetsTextView.visibility = android.view.View.VISIBLE

                } else {

                    homePetsRecyclerView.visibility = android.view.View.VISIBLE
                    noHomePetsTextView.visibility = android.view.View.GONE
                }
            }
    }

    private fun loadTasks() {

        val currentUser = auth.currentUser ?: return

        db.collection("tasks")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->

                homeTasks.clear()

                for (document in result) {

                    val task = document.toObject(CareTask::class.java)

                    task.id = document.id

                    homeTasks.add(task)
                }

                if (homeTasks.isEmpty()) {

                    noHomeTasksTextView.visibility = android.view.View.VISIBLE
                    homeTasksRecyclerView.visibility = android.view.View.GONE

                } else {

                    noHomeTasksTextView.visibility = android.view.View.GONE
                    homeTasksRecyclerView.visibility = android.view.View.VISIBLE
                }
            }
    }

    private fun setupBottomNavigation() {

        val navHomeButton =
            findViewById<com.google.android.material.button.MaterialButton>(
                R.id.navHomeButton
            )

        val navPetsButton =
            findViewById<com.google.android.material.button.MaterialButton>(
                R.id.navPetsButton
            )

        val navTasksButton =
            findViewById<com.google.android.material.button.MaterialButton>(
                R.id.navTasksButton
            )

        val navProfileButton =
            findViewById<com.google.android.material.button.MaterialButton>(
                R.id.navProfileButton
            )

        navHomeButton.setOnClickListener {
            // Already on Home
        }

        navPetsButton.setOnClickListener {
            startActivity(Intent(this, PetsActivity::class.java))
        }

        navTasksButton.setOnClickListener {
            startActivity(Intent(this, TasksActivity::class.java))
        }

        navProfileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}