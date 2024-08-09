package com.example.createpost.ui.Screens


/*import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.createpost.Model.Post
import com.example.createpost.R
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostCard(post: Post) {
    Surface {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.authorAvatarUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.ic_placeholder)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        post.authorName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                    )
                    val today = remember {
                        Date()
                    }
                    Text(
                        dateLabel(timestamp = post.timestamp, today = today),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.menu)
                    )
                }
            }
            Text(
                post.text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Spacer(Modifier.height(8.dp))
            Divider(thickness = Dp.Hairline)
            Row(Modifier.fillMaxWidth()) {
                StatusAction(
                    imageVector = Icons.Filled.ThumbUp,
                    label = stringResource(R.string.like),
                    modifier = Modifier.weight(1f)
                )
                Divider(
                    Modifier.height(48.dp),
                    thickness = Dp.Hairline
                )
                StatusAction(
                    imageVector = Icons.Filled.Comment,
                    label = stringResource(R.string.comment),
                    modifier = Modifier.weight(1f)
                )
                Divider(
                    Modifier.height(48.dp),
                    thickness = Dp.Hairline
                )
                StatusAction(
                    imageVector = Icons.Filled.Share,
                    label = stringResource(R.string.share),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

fun dateLabel(timestamp: Long, today: Date): String {
    val timestampDate = Date(timestamp)
    val calendar = Calendar.getInstance()
    calendar.time = today

    // Check if the timestamp is today
    if (isSameDay(timestampDate, today)) {
        return "Today"
    }

    // Check if the timestamp is yesterday
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    if (isSameDay(timestampDate, calendar.time)) {
        return "Yesterday"
    }

    // Check if the timestamp is tomorrow
    calendar.add(Calendar.DAY_OF_YEAR, 2)
    if (isSameDay(timestampDate, calendar.time)) {
        return "Tomorrow"
    }

    // If none of the above, return a formatted date string
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return dateFormat.format(timestampDate)
}

private fun isSameDay(date1: Date, date2: Date): Boolean {
    val calendar1 = Calendar.getInstance()
    calendar1.time = date1
    val calendar2 = Calendar.getInstance()
    calendar2.time = date2
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
}
// Implement your StatusAction composable function
@Composable
fun StatusAction(imageVector: ImageVector, label: String, modifier: Modifier) {
    // Add your status action logic here
}*/
