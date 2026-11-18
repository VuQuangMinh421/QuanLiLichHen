package com.example.quanlysach.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.quanlysach.model.Appointment

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "quanlylichhen.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_APPOINTMENTS = "appointments"
        const val COLUMN_APP_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_REMINDER = "reminder"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        val createAppointmentsTable = """
            CREATE TABLE $TABLE_APPOINTMENTS (
                $COLUMN_APP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_LOCATION TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_TIME TEXT NOT NULL,
                $COLUMN_REMINDER INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createAppointmentsTable)

        // Seed initial default user admin / 123456
        val userValues = ContentValues().apply {
            put(COLUMN_USERNAME, "admin")
            put(COLUMN_PASSWORD, "123456")
        }
        db.insert(TABLE_USERS, null, userValues)

        // Seed initial sample appointments formatted as yyyy-MM-dd
        val sample1 = ContentValues().apply {
            put(COLUMN_TITLE, "Họp nhóm đồ án")
            put(COLUMN_LOCATION, "Phòng B203")
            put(COLUMN_DATE, "2026-10-25")
            put(COLUMN_TIME, "09:00")
            put(COLUMN_REMINDER, 1)
        }
        val sample2 = ContentValues().apply {
            put(COLUMN_TITLE, "Khám răng")
            put(COLUMN_LOCATION, "Nha khoa Minh Tâm")
            put(COLUMN_DATE, "2026-10-26")
            put(COLUMN_TIME, "14:30")
            put(COLUMN_REMINDER, 1)
        }
        val sample3 = ContentValues().apply {
            put(COLUMN_TITLE, "Đi cà phê với bạn")
            put(COLUMN_LOCATION, "Highland Coffee")
            put(COLUMN_DATE, "2026-10-27")
            put(COLUMN_TIME, "19:00")
            put(COLUMN_REMINDER, 0)
        }
        db.insert(TABLE_APPOINTMENTS, null, sample1)
        db.insert(TABLE_APPOINTMENTS, null, sample2)
        db.insert(TABLE_APPOINTMENTS, null, sample3)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENTS")
        onCreate(db)
    }

    // 1. checkLogin(username, password): Trả về true nếu có dòng khớp trong bảng users.
    fun checkLogin(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // 2. insertUser(username, password): Thêm tài khoản mới vào bảng users.
    fun insertUser(username: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    // 3. insertAppointment(Appointment a): Thêm lịch hẹn, trả về id vừa thêm.
    fun insertAppointment(a: Appointment): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, a.title)
            put(COLUMN_LOCATION, a.location)
            put(COLUMN_DATE, a.date)
            put(COLUMN_TIME, a.time)
            put(COLUMN_REMINDER, if (a.reminder) 1 else 0)
        }
        return db.insert(TABLE_APPOINTMENTS, null, values)
    }

    // 4. getAllAppointments(): Lấy toàn bộ lịch hẹn, ORDER BY date ASC, time ASC.
    fun getAllAppointments(): ArrayList<Appointment> {
        val list = ArrayList<Appointment>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_APPOINTMENTS ORDER BY $COLUMN_DATE ASC, $COLUMN_TIME ASC",
            null
        )
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(COLUMN_APP_ID)
            val titleIndex = cursor.getColumnIndexOrThrow(COLUMN_TITLE)
            val locIndex = cursor.getColumnIndexOrThrow(COLUMN_LOCATION)
            val dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE)
            val timeIndex = cursor.getColumnIndexOrThrow(COLUMN_TIME)
            val remIndex = cursor.getColumnIndexOrThrow(COLUMN_REMINDER)

            do {
                val appointment = Appointment(
                    id = cursor.getInt(idIndex),
                    title = cursor.getString(titleIndex),
                    location = cursor.getString(locIndex),
                    date = cursor.getString(dateIndex),
                    time = cursor.getString(timeIndex),
                    reminder = cursor.getInt(remIndex) == 1
                )
                list.add(appointment)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // 5. getAppointmentById(int id): Lấy một lịch hẹn theo id.
    fun getAppointmentById(id: Int): Appointment? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_APPOINTMENTS WHERE $COLUMN_APP_ID = ?",
            arrayOf(id.toString())
        )
        var appointment: Appointment? = null
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(COLUMN_APP_ID)
            val titleIndex = cursor.getColumnIndexOrThrow(COLUMN_TITLE)
            val locIndex = cursor.getColumnIndexOrThrow(COLUMN_LOCATION)
            val dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE)
            val timeIndex = cursor.getColumnIndexOrThrow(COLUMN_TIME)
            val remIndex = cursor.getColumnIndexOrThrow(COLUMN_REMINDER)

            appointment = Appointment(
                id = cursor.getInt(idIndex),
                title = cursor.getString(titleIndex),
                location = cursor.getString(locIndex),
                date = cursor.getString(dateIndex),
                time = cursor.getString(timeIndex),
                reminder = cursor.getInt(remIndex) == 1
            )
        }
        cursor.close()
        return appointment
    }

    // 6. updateReminder(int id, boolean on): Cập nhật trạng thái nhắc nhở.
    fun updateReminder(id: Int, on: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_REMINDER, if (on) 1 else 0)
        }
        db.update(
            TABLE_APPOINTMENTS,
            values,
            "$COLUMN_APP_ID = ?",
            arrayOf(id.toString())
        )
    }

    // 7. deleteAppointment(int id): Xóa lịch hẹn theo id.
    fun deleteAppointment(id: Int) {
        val db = writableDatabase
        db.delete(
            TABLE_APPOINTMENTS,
            "$COLUMN_APP_ID = ?",
            arrayOf(id.toString())
        )
    }

    // 8. countAll(): Tổng số lịch hẹn: COUNT(*).
    fun countAll(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_APPOINTMENTS", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    // 9. countReminders(): Số lịch hẹn đang bật nhắc nhở: COUNT với reminder = 1.
    fun countReminders(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_APPOINTMENTS WHERE $COLUMN_REMINDER = 1",
            null
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }
}
