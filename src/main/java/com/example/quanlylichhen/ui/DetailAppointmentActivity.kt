package com.example.quanlysach.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quanlysach.db.DatabaseHelper
import com.example.quanlysach.model.Appointment

class DetailAppointmentActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var appointmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dbHelper = DatabaseHelper(this)

        appointmentId = intent.getIntExtra("APPOINTMENT_ID", -1)
        val appointment = dbHelper.getAppointmentById(appointmentId)

        if (appointment == null) {
            Toast.makeText(this, "Không tìm thấy lịch hẹn", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            DetailAppointmentScreen(
                initialAppointment = appointment,
                onBackClick = { finish() },
                onReminderToggle = { isChecked ->
                    dbHelper.updateReminder(appointmentId, isChecked)
                },
                onDeleteClick = {
                    dbHelper.deleteAppointment(appointmentId)
                    Toast.makeText(this, "Đã xóa lịch hẹn", Toast.LENGTH_SHORT).show()
                    finish()
                }
            )
        }
    }
}

@Composable
fun DetailAppointmentScreen(
    initialAppointment: Appointment,
    onBackClick: () -> Unit,
    onReminderToggle: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    var isReminderOn by remember { mutableStateOf(initialAppointment.reminder) }

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
                    .background(PrimaryTealColor)
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
                    text = "Chi tiết lịch hẹn",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Card displaying Appointment Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // 1. Title
                        Text(
                            text = initialAppointment.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Địa điểm
                        Text(
                            text = "Địa điểm",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = initialAppointment.location,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. Thời gian
                        Text(
                            text = "Thời gian",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${initialAppointment.displayDate}  ·  ${initialAppointment.time}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // 4. Nhắc nhở + Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Nhắc nhở",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isReminderOn) "Đang bật" else "Đang tắt",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Switch(
                                checked = isReminderOn,
                                onCheckedChange = { checked ->
                                    isReminderOn = checked
                                    onReminderToggle(checked)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryTealColor,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 5. Button Quay lại (Outline Teal)
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, PrimaryTealColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryTealColor)
                ) {
                    Text(
                        text = "Quay lại",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 6. Button Xóa lịch hẹn (Red)
                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(
                        text = "Xóa lịch hẹn",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
