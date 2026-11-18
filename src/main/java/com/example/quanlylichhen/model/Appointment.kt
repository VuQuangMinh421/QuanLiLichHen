package com.example.quanlysach.model

data class Appointment(
    val id: Int = 0,
    val title: String,
    val location: String,
    val date: String, // stored in database as yyyy-MM-dd
    val time: String, // stored in database as HH:mm
    val reminder: Boolean
) {
    // Formats yyyy-MM-dd to dd/MM/yyyy for UI display
    val displayDate: String
        get() {
            return try {
                val parts = date.split("-")
                if (parts.size == 3) {
                    "${parts[2]}/${parts[1]}/${parts[0]}"
                } else {
                    date
                }
            } catch (e: Exception) {
                date
            }
        }
}
