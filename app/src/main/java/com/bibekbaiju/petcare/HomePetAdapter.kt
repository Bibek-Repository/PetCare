package com.bibekbaiju.petcare

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class HomePetAdapter(
    private val pets: List<Pet>,
    private val onPetClick: (Pet) -> Unit
) : RecyclerView.Adapter<HomePetAdapter.HomePetViewHolder>() {

    class HomePetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val petImageView: ImageView =
            itemView.findViewById(R.id.homePetImageView)

        val petNameTextView: TextView =
            itemView.findViewById(R.id.homePetNameTextView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomePetViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_pet, parent, false)

        return HomePetViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HomePetViewHolder,
        position: Int
    ) {

        val pet = pets[position]

        holder.petNameTextView.text = pet.name

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

        holder.itemView.setOnClickListener {
            onPetClick(pet)
        }
    }

    override fun getItemCount(): Int {
        return pets.size
    }
}