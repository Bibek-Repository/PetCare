package com.bibekbaiju.petcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeTaskAdapter(
    private val tasks: List<CareTask>,
    private val onTaskClick: (CareTask) -> Unit
) : RecyclerView.Adapter<HomeTaskAdapter.HomeTaskViewHolder>() {

    class HomeTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView =
            itemView.findViewById(R.id.homeTaskTitleTextView)

        val petTextView: TextView =
            itemView.findViewById(R.id.homeTaskPetTextView)

        val scheduleTextView: TextView =
            itemView.findViewById(R.id.homeTaskScheduleTextView)

        val statusTextView: TextView =
            itemView.findViewById(R.id.homeTaskStatusTextView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeTaskViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_task, parent, false)

        return HomeTaskViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HomeTaskViewHolder,
        position: Int
    ) {

        val task = tasks[position]

        holder.titleTextView.text = task.title

        holder.petTextView.text =
            "Pet: ${task.petName}"

        holder.scheduleTextView.text =
            "${task.frequency} • ${task.time}"

        if (task.completed) {
            holder.statusTextView.text = "✓ Completed"
        } else {
            holder.statusTextView.text = "○ Pending"
        }

        holder.itemView.setOnClickListener {
            onTaskClick(task)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}