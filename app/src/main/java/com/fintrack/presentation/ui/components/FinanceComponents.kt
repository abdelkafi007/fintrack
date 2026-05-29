package com.fintrack.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fintrack.core.utils.CurrencyFormatter
import com.fintrack.domain.model.TransactionType
import com.fintrack.presentation.ui.theme.*

/**
 * Animated amount text with currency formatting and color coding.
 */
@Composable
fun AmountText(
    amount: Double,
    modifier: Modifier = Modifier,
    type: TransactionType? = null,
    currencyCode: String = "USD",
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleLarge,
    showSign: Boolean = true,
    animate: Boolean = true
) {
    val color = when (type) {
        TransactionType.INCOME -> IncomeGreen
        TransactionType.EXPENSE -> ExpenseRose
        TransactionType.TRANSFER -> TransferBlue
        null -> MaterialTheme.colorScheme.onSurface
    }

    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(300),
        label = "amount_color"
    )

    val displayAmount = if (animate) {
        val animatedValue by animateFloatAsState(
            targetValue = amount.toFloat(),
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "amount_value"
        )
        animatedValue.toDouble()
    } else {
        amount
    }

    val prefix = when {
        !showSign -> ""
        type == TransactionType.INCOME -> "+"
        type == TransactionType.EXPENSE -> "-"
        amount > 0 -> "+"
        else -> ""
    }

    val absAmount = kotlin.math.abs(displayAmount)
    val formatted = CurrencyFormatter.format(absAmount, currencyCode)

    Text(
        text = "$prefix$formatted",
        modifier = modifier,
        style = style,
        fontWeight = FontWeight.SemiBold,
        color = animatedColor
    )
}

/**
 * Category chip with icon and color dot.
 */
@Composable
fun CategoryChip(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val chipColors = if (selected) {
        FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.15f),
            selectedLabelColor = color
        )
    } else {
        FilterChipDefaults.filterChipColors()
    }

    FilterChip(
        selected = selected,
        onClick = { onClick?.invoke() },
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(name, style = MaterialTheme.typography.labelMedium)
            }
        },
        modifier = modifier,
        colors = chipColors,
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Animated progress bar for budgets and goals with gradient fill.
 */
@Composable
fun FinTrackProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    isOverBudget: Boolean = false,
    gradientColors: List<Color> = listOf(Primary, Secondary),
    height: Int = 10,
    showLabel: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val barColors = if (isOverBudget) {
        listOf(ExpenseRose, ExpenseRoseDark)
    } else {
        gradientColors
    }

    Column(modifier = modifier) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOverBudget) ExpenseRose else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .clip(RoundedCornerShape(height.dp / 2))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(height.dp / 2))
                    .background(
                        Brush.horizontalGradient(barColors)
                    )
            )
        }
    }
}

/**
 * Circular progress indicator for savings goals.
 */
@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Primary,
    strokeWidth: Float = 12f,
    label: @Composable () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "circular_progress"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(100.dp),
            color = color,
            trackColor = color.copy(alpha = 0.15f),
            strokeWidth = strokeWidth.dp,
        )
        label()
    }
}

/**
 * Mini summary card for income/expense display.
 */
@Composable
fun SummaryMiniCard(
    label: String,
    amount: Double,
    type: TransactionType,
    modifier: Modifier = Modifier,
    currencyCode: String = "USD"
) {
    val bgColor = when (type) {
        TransactionType.INCOME -> IncomeGreen.copy(alpha = 0.1f)
        TransactionType.EXPENSE -> ExpenseRose.copy(alpha = 0.1f)
        TransactionType.TRANSFER -> TransferBlue.copy(alpha = 0.1f)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            AmountText(
                amount = amount,
                type = type,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.titleMedium,
                showSign = true
            )
        }
    }
}
