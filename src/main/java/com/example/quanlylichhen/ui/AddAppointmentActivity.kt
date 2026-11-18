package com.example.quanlysach.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quanlysach.db.DatabaseHelper
import com.example.quanlysach.model.Appointment

class AddAppointmentActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dbHelper = DatabaseHelper(this)

        setContent {
            AddAppointmentScreen(
                onBackClick = { finish() },
                onSaveClick = { title, location, dateStr, timeStr, reminder ->
                    if (title.isBlank()) {
                        Toast.makeText(this, "Vui lòng nhập tên lịch hẹn", Toast.LENGTH_SHORT).show()
                    } else if (location.isBlank()) {
                        Toast.makeText(this, "Vui lòng nhập địa điểm", Toast.LENGTH_SHORT).show()
                    } else {
                        val appointment = Appointment(
                            title = title.trim(),
                            location = location.trim(),
                            date = dateStr,
                            time = timeStr,
                            reminder = reminder
                        )
                        dbHelper.insertAppointment(appointment)
                        Toast.makeText(this, "Đã lưu lịch hẹn", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            )
        }
    }
}

@Composable
fun AddAppointmentScreen(
    onBackClick: () -> Unit,
    onSaveClick: (String, String, String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Date selection states: Day (25), Month (10), Year (2026)
    var selectedDay by remember { mutableIntStateOf(25) }
    var selectedMonth by remember { mutableIntStateOf(10) }
    var selectedYear by remember { mutableIntStateOf(2026) }

    // Time selection states: Hour (09), Minute (30)
    var selectedHour by remember { mutableIntStateOf(9) }
    var selectedMinute by remember { mutableIntStateOf(30) }

    var reminder by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Top Bar with Back Arrow and Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 44.dp, bottom = 12.dp, start = 8.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Thêm lịch hẹn",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {

                Text(
                    text = "Tên lịch hẹn",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Ví dụ: Họp nhóm đồ án", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTealColor,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))


                Text(
                    text = "Địa điểm",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Ví dụ: Phòng B203", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 3. Ngày hẹn
                Text(
                    text = "Ngày hẹn",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date Selector Wheels UI matching Image 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    DatePickerWheel(
                        items = (1..31).map { it.toString() },
                        selectedIndex = selectedDay - 1,
                        onItemSelected = { selectedDay = it + 1 },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))


                    DatePickerWheel(
                        items = (1..12).map { "Th $it" },
                        selectedIndex = selectedMonth - 1,
                        onItemSelected = { selectedMonth = it + 1 },
                        modifier = Modifier.weight(1.1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    DatePickerWheel(
                        items = (2024..2030).map { it.toString() },
                        selectedIndex = (selectedYear - 2024).coerceIn(0, 6),
                        onItemSelected = { selectedYear = 2024 + it },
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))


                Text(
                    text = "Giờ hẹn",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TimePickerWheel(
                        items = (0..23).map { String.format("%02d", it) },
                        selectedIndex = selectedHour,
                        onItemSelected = { selectedHour = it },
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Minute Wheel
                    TimePickerWheel(
                        items = (0..59).map { String.format("%02d", it) },
                        selectedIndex = selectedMinute,
                        onItemSelected = { selectedMinute = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nhắc nhở trước giờ hẹn",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )

                    Switch(
                        checked = reminder,
                        onCheckedChange = { reminder = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,

                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))


                Button(
                    onClick = {
                        val dateStr = String.format(java.util.Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay)
                        val timeStr = String.format(java.util.Locale.US, "%02d:%02d", selectedHour, selectedMinute)
                        onSaveClick(title, location, dateStr, timeStr, reminder)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text(
                        text = "Lưu lịch hẹn",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun DatePickerWheel(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE2E8F0), shape = RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val safeIdx = selectedIndex.coerceIn(0, items.size - 1)
        val prev = if (safeIdx > 0) items[safeIdx - 1] else ""
        val current = items[safeIdx]
        val next = if (safeIdx < items.size - 1) items[safeIdx + 1] else ""

        // Previous item
        Text(
            text = prev,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { if (safeIdx > 0) onItemSelected(safeIdx - 1) }
        )

        // Selected item
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE0F2F1))
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = current,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D40)
            )
        }

        // Next item
        Text(
            text = next,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { if (safeIdx < items.size - 1) onItemSelected(safeIdx + 1) }
        )
    }
}

@Composable
fun TimePickerWheel(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE2E8F0), shape = RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val safeIdx = selectedIndex.coerceIn(0, items.size - 1)
        val prev = if (safeIdx > 0) items[safeIdx - 1] else ""
        val current = items[safeIdx]
        val next = if (safeIdx < items.size - 1) items[safeIdx + 1] else ""

        // Previous item
        Text(
            text = prev,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { if (safeIdx > 0) onItemSelected(safeIdx - 1) }
        )

        // Selected item
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE0F2F1))
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = current,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D40)
            )
        }

        // Next item
        Text(
            text = next,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .clickable { if (safeIdx < items.size - 1) onItemSelected(safeIdx + 1) }
        )
    }
}
