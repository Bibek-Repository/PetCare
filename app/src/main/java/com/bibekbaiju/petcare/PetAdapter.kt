package com.bibekbaiju.petcare

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.io.File

class PetAdapter(
    private val pets: List<Pet>,
    private val onEditClick: (Pet) -> Unit,
    private val onDeleteClick: (Pet) -> Unit
) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val petImageView: ImageView =
            itemView.findViewById(R.id.petImageView)

        val petNameTextView: TextView =
            itemView.findViewById(R.id.petNameTextView)

        val petDetailsTextView: TextView =
            itemView.findViewById(R.id.petDetailsTextView)

        val petNotesTextView: TextView =
            itemView.findViewById(R.id.petNotesTextView)

        val editPetButton: MaterialButton =
            itemView.findViewById(R.id.editPetButton)

        val deletePetButton: MaterialButton =
            itemView.findViewById(R.id.deletePetButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PetViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pet, parent, false)

        return PetViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PetViewHolder,
        position: Int
    ) {

        val pet = pets[position]

        // Load pet image
        if (pet.imagePath.isNotEmpty()) {

            val imageFile = File(pet.imagePath)

            if (imageFile.exists()) {

                val bitmap =
                    BitmapFactory.decodeFile(imageFile.absolutePath)

                holder.petImageView.setImageBitmap(bitmap)

            } else {

                holder.petImageView.setImageResource(
                    android.R.drawable.ic_menu_camera
                )
            }

        } else {

            holder.petImageView.setImageResource(
                android.R.drawable.ic_menu_camera
            )
        }

        holder.petNameTextView.text = pet.name

        holder.petDetailsTextView.text =
            "${pet.species} • ${pet.breed} • ${pet.age} years"

        holder.petNotesTextView.text =
            if (pet.notes.isEmpty()) {
                "No notes"
            } else {
                pet.notes
            }

        holder.editPetButton.setOnClickListener {
            onEditClick(pet)
        }

        holder.deletePetButton.setOnClickListener {
            onDeleteClick(pet)
        }
    }

    override fun getItemCount(): Int {
        return pets.size
    }
}