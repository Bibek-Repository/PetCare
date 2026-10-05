package com.bibekbaiju.petcare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val addPetButton =
            findViewById<MaterialButton>(R.id.addPetButton)

        addPetButton.setOnClickListener {
            startActivity(
                Intent(this, AddPetActivity::class.java)
            )
        }
        val viewPetsButton =
            findViewById<MaterialButton>(R.id.viewPetsButton)

        viewPetsButton.setOnClickListener {
            startActivity(Intent(this, PetsActivity::class.java))
        }
    }
}