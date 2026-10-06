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

class TasksActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var adapter: TaskAdapter

    private val tasks = mutableListOf<CareTask>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.tasksRecyclerView)
        emptyTextView = findViewById(R.id.emptyTextView)

        val addTaskButton =
            findViewById<MaterialButton>(R.id.addTaskButton)

        adapter = TaskAdapter(
            tasks,

            // Checkbox
            onCompletedChange = { task, completed ->
                updateTaskCompleted(task, completed)
            },

            // Edit
            onEditClick = { task ->
                val intent = Intent(this, EditTaskActivity::class.java)
                intent.putExtra("taskId", task.id)
                startActivity(intent)
            },

            // Delete
            onDeleteClick = { task ->
                deleteTask(task)
            }
        )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        addTaskButton.setOnClickListener {
            startActivity(
                Intent(this, AddTaskActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {

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

        db.collection("tasks")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->

                tasks.clear()

                for (document in result) {

                    val task =
                        document.toObject(CareTask::class.java)

                    task.id = document.id

                    tasks.add(task)
                }

                adapter.notifyDataSetChanged()

                if (tasks.isEmpty()) {

                    emptyTextView.visibility =
                        View.VISIBLE

                    recyclerView.visibility =
                        View.GONE

                } else {

                    emptyTextView.visibility =
                        View.GONE

                    recyclerView.visibility =
                        View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to load tasks: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun updateTaskCompleted(
        task: CareTask,
        completed: Boolean
    ) {

        db.collection("tasks")
            .document(task.id)
            .update("completed", completed)
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to update task: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun deleteTask(task: CareTask) {

        db.collection("tasks")
            .document(task.id)
            .delete()
            .addOnSuccessListener {

                tasks.remove(task)

                adapter.notifyDataSetChanged()

                if (tasks.isEmpty()) {

                    emptyTextView.visibility =
                        View.VISIBLE

                    recyclerView.visibility =
                        View.GONE
                }

                Toast.makeText(
                    this,
                    "${task.title} deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Failed to delete task: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}