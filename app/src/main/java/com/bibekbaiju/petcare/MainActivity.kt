package com.bibekbaiju.petcare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var todayTasksRecyclerView: RecyclerView
    private lateinit var noTodayTasksTextView: TextView
    private lateinit var taskAdapter: TaskAdapter

    private val todayTasks = mutableListOf<CareTask>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Buttons
        val addPetButton =
            findViewById<MaterialButton>(R.id.addPetButton)

        val viewTasksButton =
            findViewById<MaterialButton>(R.id.viewTasksButton)

        val viewPetsButton =
            findViewById<MaterialButton>(R.id.viewPetsButton)

        addPetButton.setOnClickListener {
            startActivity(
                Intent(this, AddPetActivity::class.java)
            )
        }

        viewTasksButton.setOnClickListener {
            startActivity(
                Intent(this, TasksActivity::class.java)
            )
        }

        viewPetsButton.setOnClickListener {
            startActivity(
                Intent(this, PetsActivity::class.java)
            )
        }

        // Today's Tasks
        todayTasksRecyclerView =
            findViewById(R.id.todayTasksRecyclerView)

        noTodayTasksTextView =
            findViewById(R.id.noTodayTasksTextView)

        taskAdapter = TaskAdapter(
            todayTasks,

            // Completed
            onCompletedChange = { task, completed ->
                updateTaskCompleted(task, completed)
            },

            // Edit
            onEditClick = { task ->
                val intent =
                    Intent(this, EditTaskActivity::class.java)

                intent.putExtra(
                    "taskId",
                    task.id
                )

                startActivity(intent)
            },

            // Delete
            onDeleteClick = { task ->
                deleteTask(task)
            },

            // Delegate
            onDelegateClick = { task ->
                delegateTaskBySms(task)
            }
        )

        todayTasksRecyclerView.layoutManager =
            LinearLayoutManager(this)

        todayTasksRecyclerView.adapter =
            taskAdapter
    }

    override fun onResume() {
        super.onResume()
        loadTodayTasks()
    }

    private fun loadTodayTasks() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            return
        }

        db.collection("tasks")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->

                todayTasks.clear()

                for (document in result) {

                    val task =
                        document.toObject(CareTask::class.java)

                    task.id = document.id

                    /*
                     * For now, Daily tasks are shown
                     * on the dashboard.
                     */
                    if (task.frequency == "Daily") {
                        todayTasks.add(task)
                    }
                }

                taskAdapter.notifyDataSetChanged()

                if (todayTasks.isEmpty()) {

                    todayTasksRecyclerView.visibility =
                        View.GONE

                    noTodayTasksTextView.visibility =
                        View.VISIBLE

                } else {

                    todayTasksRecyclerView.visibility =
                        View.VISIBLE

                    noTodayTasksTextView.visibility =
                        View.GONE
                }
            }
    }

    private fun updateTaskCompleted(
        task: CareTask,
        completed: Boolean
    ) {

        db.collection("tasks")
            .document(task.id)
            .update("completed", completed)
    }

    private fun deleteTask(task: CareTask) {

        db.collection("tasks")
            .document(task.id)
            .delete()
            .addOnSuccessListener {

                todayTasks.remove(task)

                taskAdapter.notifyDataSetChanged()

                if (todayTasks.isEmpty()) {

                    todayTasksRecyclerView.visibility =
                        View.GONE

                    noTodayTasksTextView.visibility =
                        View.VISIBLE
                }
            }
    }

    private fun delegateTaskBySms(task: CareTask) {

        val message = """
            Hi, can you help with this pet care task?

            Pet: ${task.petName}
            Task: ${task.title}
            Schedule: ${task.frequency} • ${task.time}
            Supplies: ${if (task.supplies.isEmpty()) "None" else task.supplies}
            Notes: ${if (task.notes.isEmpty()) "None" else task.notes}
        """.trimIndent()

        val intent =
            Intent(Intent.ACTION_SENDTO)

        intent.data =
            android.net.Uri.parse("smsto:")

        intent.putExtra(
            "sms_body",
            message
        )

        try {
            startActivity(intent)
        } catch (exception: Exception) {

            // No SMS application available
        }
    }
}