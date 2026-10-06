package com.bibekbaiju.petcare

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class TaskAdapter(
    private val tasks: List<CareTask>,
    private val onCompletedChange: (CareTask, Boolean) -> Unit,
    private val onEditClick: (CareTask) -> Unit,
    private val onDeleteClick: (CareTask) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val taskTitleTextView: TextView =
            itemView.findViewById(R.id.taskTitleTextView)

        val taskPetTextView: TextView =
            itemView.findViewById(R.id.taskPetTextView)

        val taskScheduleTextView: TextView =
            itemView.findViewById(R.id.taskScheduleTextView)

        val taskSuppliesTextView: TextView =
            itemView.findViewById(R.id.taskSuppliesTextView)

        val taskNotesTextView: TextView =
            itemView.findViewById(R.id.taskNotesTextView)

        val taskCompletedCheckBox: CheckBox =
            itemView.findViewById(R.id.taskCompletedCheckBox)

        val editTaskButton: MaterialButton =
            itemView.findViewById(R.id.editTaskButton)

        val deleteTaskButton: MaterialButton =
            itemView.findViewById(R.id.deleteTaskButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)

        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TaskViewHolder,
        position: Int
    ) {

        val task = tasks[position]

        holder.taskTitleTextView.text = task.title

        holder.taskPetTextView.text =
            "🐾 ${task.petName}"

        holder.taskScheduleTextView.text =
            "${task.frequency} • ${task.time}"

        holder.taskSuppliesTextView.text =
            if (task.supplies.isEmpty()) {
                "Supplies: None"
            } else {
                "Supplies: ${task.supplies}"
            }

        holder.taskNotesTextView.text =
            if (task.notes.isEmpty()) {
                "No notes"
            } else {
                task.notes
            }

        // Prevent the listener from firing while RecyclerView reuses the view
        holder.taskCompletedCheckBox.setOnCheckedChangeListener(null)

        holder.taskCompletedCheckBox.isChecked = task.completed

        updateCompletedAppearance(
            holder,
            task.completed
        )

        // Completed checkbox
        holder.taskCompletedCheckBox.setOnCheckedChangeListener { _, isChecked ->

            task.completed = isChecked

            updateCompletedAppearance(
                holder,
                isChecked
            )

            onCompletedChange(
                task,
                isChecked
            )
        }

        // Edit button
        holder.editTaskButton.setOnClickListener {
            onEditClick(task)
        }

        // Delete button
        holder.deleteTaskButton.setOnClickListener {
            onDeleteClick(task)
        }
    }

    private fun updateCompletedAppearance(
        holder: TaskViewHolder,
        completed: Boolean
    ) {

        if (completed) {

            holder.taskTitleTextView.paintFlags =
                holder.taskTitleTextView.paintFlags or
                        Paint.STRIKE_THRU_TEXT_FLAG

            holder.taskCompletedCheckBox.text =
                "Completed ✓"

        } else {

            holder.taskTitleTextView.paintFlags =
                holder.taskTitleTextView.paintFlags and
                        Paint.STRIKE_THRU_TEXT_FLAG.inv()

            holder.taskCompletedCheckBox.text =
                "Completed"
        }
    }

    override fun getItemCount(): Int =
        tasks.size
}