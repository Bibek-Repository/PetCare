package com.bibekbaiju.petcare

data class CareTask(
    var id: String = "",
    var petId: String = "",
    var petName: String = "",
    var title: String = "",
    var frequency: String = "",
    var time: String = "",
    var supplies: String = "",
    var notes: String = "",
    var completed: Boolean = false,
    var userId: String = ""
)