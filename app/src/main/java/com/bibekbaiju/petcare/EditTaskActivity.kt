package com.bibekbaiju.petcare

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class EditTaskActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var taskTitleEditText: TextInputEditText
    private lateinit var frequencySpinner: Spinner
    private lateinit var timeEditText: TextInputEditText
    private lateinit var suppliesEditText: TextInputEditText
    private lateinit var notesEditText: TextInputEditText
    private lateinit var updateTaskButton: MaterialButton

    private var taskId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_task)

        db = FirebaseFirestore.getInstance()

        taskTitleEditText =
            findViewById(R.id.taskTitleEditText)

        frequencySpinner =
            findViewById(R.id.frequencySpinner)

        timeEditText =
            findViewById(R.id.timeEditText)

        suppliesEditText =
            findViewById(R.id.suppliesEditText)

        notesEditText =
            findViewById(R.id.notesEditText)

        updateTaskButton =
            findViewById(R.id.updateTaskButton)

        taskId =
            intent.getStringExtra("taskId") ?: ""

        if (taskId.isEmpty()) {

            Toast.makeText(
                this,
                "Task information not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setupFrequencySpinner()

        loadTask()

        updateTaskButton.setOnClickListener {
            updateTask()
        }
    }

    private fun setupFrequencySpinner() {

        val frequencies =
            listOf("Daily", "Weekly")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            frequencies
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        frequencySpinner.adapter = adapter
    }

    private fun loadTask() {

        db.collection("tasks")
            .document(taskId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    taskTitleEditText.setText(
                        document.getString("title") ?: ""
                    )

                    timeEditText.setText(
                        document.getString("time") ?: ""
                    )

                    suppliesEditText.setText(
                        document.getString("supplies") ?: ""
                    )

                    notesEditText.setText(
                        document.getString("notes") ?: ""
                    )

                    val frequency =
                        document.getString("frequency") ?: "Daily"

                    val position =
                        if (frequency == "Weekly") 1 else 0

                    frequencySpinner.setSelection(position)

                } else {

                    Toast.makeText(
                        this,
                        "Task not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load task: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun updateTask() {

        val title =
            taskTitleEditText.text.toString().trim()

        val frequency =
            frequencySpinner.selectedItem.toString()

        val time =
            timeEditText.text.toString().trim()

        val supplies =
            suppliesEditText.text.toString().trim()

        val notes =
            notesEditText.text.toString().trim()

        if (title.isEmpty()) {

            taskTitleEditText.error =
                "Please enter the task name"

            taskTitleEditText.requestFocus()

            return
        }

        updateTaskButton.isEnabled = false
        updateTaskButton.text = "Updating..."

        val updates = hashMapOf<String, Any>(
            "title" to title,
            "frequency" to frequency,
            "time" to time,
            "supplies" to supplies,
            "notes" to notes
        )

        db.collection("tasks")
            .document(taskId)
            .update(updates)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Care routine updated!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener { exception ->

                updateTaskButton.isEnabled = true
                updateTaskButton.text = "Update Routine"

                Toast.makeText(
                    this,
                    "Failed to update routine: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}