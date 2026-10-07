package com.bibekbaiju.petcare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val navHomeButton =
            findViewById<MaterialButton>(R.id.navHomeButton)

        val navPetsButton =
            findViewById<MaterialButton>(R.id.navPetsButton)

        val navTasksButton =
            findViewById<MaterialButton>(R.id.navTasksButton)

        val navProfileButton =
            findViewById<MaterialButton>(R.id.navProfileButton)

        navHomeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        navPetsButton.setOnClickListener {
            startActivity(Intent(this, PetsActivity::class.java))
            finish()
        }

        navTasksButton.setOnClickListener {
            startActivity(Intent(this, TasksActivity::class.java))
            finish()
        }

        navProfileButton.setOnClickListener {
            // Already on Profile
        }
    }
}