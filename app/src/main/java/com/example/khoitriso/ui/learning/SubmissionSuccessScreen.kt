package com.example.khoitriso.ui.learning

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.khoitriso.domain.models.AssignmentSubmission

@Composable
fun SubmissionSuccessScreen(
    submission: AssignmentSubmission,
    onBackToLesson: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Icon
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Thành công",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = "Nộp bài thành công!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Score Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Điểm số",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${submission.score ?: 0} / ${submission.maxScore ?: 0}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Correct answers count
                val correctCount = submission.answers.count { it.isCorrect }
                val totalQuestions = submission.answers.size
                Text(
                    text = "Đúng $correctCount / $totalQuestions câu",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                // Attempt number
                Text(
                    text = "Lần nộp thứ ${submission.attemptNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }

        // Feedback if available
        submission.feedback?.let { feedback ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Nhận xét",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Back to Lesson Button
        Button(
            onClick = onBackToLesson,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Quay lại bài học",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
