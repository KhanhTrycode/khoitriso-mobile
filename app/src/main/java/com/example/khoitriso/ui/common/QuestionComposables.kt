package com.example.khoitriso.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.khoitriso.R
import com.example.khoitriso.domain.models.Option
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.ui.forum.KatexHtmlContent
import com.example.khoitriso.utils.QuestionType

@Composable
fun QuestionItem(
    question: Question, 
    onOptionSelected: (Int, Int) -> Unit,
    questionNumber: Int? = null // Số thứ tự câu hỏi (đã bỏ GroupTitle)
) {
    // GroupTitle được hiển thị như một tiêu đề nhóm, không phải câu hỏi
    if (QuestionType.fromInt(question.questionType) == QuestionType.GroupTitle) {
        GroupTitleItem(question = question)
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.question_number, 
                                    questionNumber ?: question.orderIndex
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    // Hiển thị nội dung câu hỏi với hỗ trợ hình ảnh và bảng
                    Box(modifier = Modifier.fillMaxWidth()) {
                        KatexHtmlContent(
                            html = question.questionContent,
                            textSizeSp = 16f
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                OptionList(
                    questionType = question.questionType,
                    options = question.options,
                    onOptionSelected = {
                        onOptionSelected(it, question.id)
                    }
                )
            }
        }
    }
}

@Composable
fun OptionList(
    questionType: Int,
    options: List<Option>,
    onOptionSelected: (Int) -> Unit,
) {
    when (QuestionType.fromInt(questionType)) {
        QuestionType.MultipleChoice -> MultipleChoiceOptionList(options, onOptionSelected)
        QuestionType.TrueFalse -> TrueFalseOptionList(options, onOptionSelected)
        QuestionType.ShortAnswer -> ShortAnswerInput()
        QuestionType.GroupTitle -> {
            // GroupTitle không có options, không hiển thị gì ở đây
            Spacer(modifier = Modifier.height(0.dp))
        }
        else -> Text("Loại câu hỏi chưa hỗ trợ", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun MultipleChoiceOptionList(options: List<Option>, onOptionSelected: (Int) -> Unit) {
    val (selectedOptionId, setSelectedOption) = remember { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            val isSelected = option.id == selectedOptionId

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                label = "bgColor"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                label = "borderColor"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            setSelectedOption(option.id)
                            onOptionSelected(option.id)
                        }
                    ),
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor,
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        KatexHtmlContent(html = option.optionText)
                    }
                }
            }
        }
    }
}

@Composable
fun TrueFalseOptionList(
    options: List<Option>,
    onOptionSelected: (Int) -> Unit,
) {
    val (selectedId, setSelected) = remember { mutableStateOf<Int?>(null) }
    val trueOption = options.firstOrNull { it.optionText.equals("Đúng", ignoreCase = true) || it.optionText.equals("True", ignoreCase = true) }
    val falseOption = options.firstOrNull { it.optionText.equals("Sai", ignoreCase = true) || it.optionText.equals("False", ignoreCase = true) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (trueOption != null) {
            TrueFalseCard(
                text = "Đúng",
                isSelected = selectedId == trueOption.id,
                icon = Icons.Rounded.Check,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
                onClick = {
                    setSelected(trueOption.id)
                    onOptionSelected(trueOption.id)
                }
            )
        }
        if (falseOption != null) {
            TrueFalseCard(
                text = "Sai",
                isSelected = selectedId == falseOption.id,
                icon = Icons.Rounded.Close,
                color = Color(0xFFE53935),
                modifier = Modifier.weight(1f),
                onClick = {
                    setSelected(falseOption.id)
                    onOptionSelected(falseOption.id)
                }
            )
        }
    }
}

@Composable
fun TrueFalseCard(
    text: String,
    isSelected: Boolean,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
    )
    val borderColor by animateColorAsState(
        if (isSelected) color else MaterialTheme.colorScheme.outlineVariant
    )

    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ShortAnswerInput() {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(stringResource(R.string.enter_answer)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            Icon(Icons.Rounded.Edit, contentDescription = null)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
fun GroupTitleItem(question: Question) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Divider phía trên
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            thickness = 2.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Card GroupTitle với style nổi bật hơn
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                3.dp,
                MaterialTheme.colorScheme.tertiary
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon lớn hơn và nổi bật hơn
                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Label,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Nội dung GroupTitle
                Box(modifier = Modifier.weight(1f)) {
                    KatexHtmlContent(
                        html = question.questionContent,
                        textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        textSizeSp = 20f
                    )
                }
            }
        }
        
        // Divider phía dưới
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            thickness = 2.dp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

