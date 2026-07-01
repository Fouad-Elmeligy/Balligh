package com.example.ballighandroidapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ballighandroidapp.helpers.local.data.entities.NotificationEntity
import com.example.ballighandroidapp.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.remember

@Composable
fun NotificationItem(
    notification: NotificationEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedTime = remember(notification.timeDelivered) {
        val sdf = SimpleDateFormat("hh:mm a | yyyy/MM/dd", Locale.getDefault())
        sdf.format(Date(notification.timeDelivered))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            // إذا كان الإشعار غير مقروء (false) نضع خلفية ملونة خفيفة لتمييزه
            containerColor = if (notification.isRead) Color.White else Color(0xFFEFF6FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 🔵 نقطة زرقاء جانبية تظهر فقط للإشعارات غير المقروءة
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp, end = 12.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Primary)
                )
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تحديث جديد", // عنوان افتراضي أو تصنيفي لأن الرسالة مدمجة
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )

                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = Color(0xFF475569),
                    lineHeight = 20.sp
                )
            }
        }
    }
}