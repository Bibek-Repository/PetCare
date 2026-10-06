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

        setupTaskGestures()
    }

    private fun setupTaskGestures() {

        val itemTouchHelper = androidx.recyclerview.widget.ItemTouchHelper(
            object : androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(
                0,
                androidx.recyclerview.widget.ItemTouchHelper.LEFT or
                        androidx.recyclerview.widget.ItemTouchHelper.RIGHT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {

                    val position =
                        viewHolder.bindingAdapterPosition

                    if (position == RecyclerView.NO_POSITION) {
                        return
                    }

                    val task = tasks[position]

                    if (direction ==
                        androidx.recyclerview.widget.ItemTouchHelper.RIGHT
                    ) {

                        // Swipe right = mark completed
                        updateTaskCompleted(task, true)

                        task.completed = true

                        adapter.notifyItemChanged(position)

                        Toast.makeText(
                            this@TasksActivity,
                            "${task.title} marked completed",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (direction ==
                        androidx.recyclerview.widget.ItemTouchHelper.LEFT
                    ) {

                        // Swipe left = delete
                        deleteTask(task)
                    }
                }
            }
        )

        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.addOnItemTouchListener(
            object : RecyclerView.SimpleOnItemTouchListener() {

                private var downTime = 0L

                override fun onInterceptTouchEvent(
                    rv: RecyclerView,
                    e: android.view.MotionEvent
                ): Boolean {

                    when (e.actionMasked) {

                        android.view.MotionEvent.ACTION_DOWN -> {
                            downTime = System.currentTimeMillis()
                        }

                        android.view.MotionEvent.ACTION_UP -> {

                            val duration =
                                System.currentTimeMillis() - downTime

                            if (duration >= 600) {

                                val child =
                                    rv.findChildViewUnder(e.x, e.y)

                                if (child != null) {

                                    val position =
                                        rv.getChildAdapterPosition(child)

                                    if (position != RecyclerView.NO_POSITION) {

                                        val task = tasks[position]

                                        val intent = Intent(
                                            this@TasksActivity,
                                            EditTaskActivity::class.java
                                        )

                                        intent.putExtra(
                                            "taskId",
                                            task.id
                                        )

                                        startActivity(intent)

                                        return true
                                    }
                                }
                            }
                        }
                    }

                    return false
                }
            }
        )
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