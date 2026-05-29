package com.fintrack.presentation.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fintrack.core.utils.CurrencyFormatter
import com.fintrack.core.utils.DateFormatter
import com.fintrack.domain.model.TransactionType
import com.fintrack.presentation.ui.components.*
import com.fintrack.presentation.ui.theme.*
import com.fintrack.presentation.viewmodel.AnalyticsViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics", fontWeight = FontWeight.Bold) },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Text(
                            DateFormatter.formatMonthYear(LocalDate.now()),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Income vs Expense Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryMiniCard(
                    label = "Income",
                    amount = state.monthlyIncome,
                    type = TransactionType.INCOME,
                    modifier = Modifier.weight(1f)
                )
                SummaryMiniCard(
                    label = "Expense",
                    amount = state.monthlyExpense,
                    type = TransactionType.EXPENSE,
                    modifier = Modifier.weight(1f)
                )
            }

            // Net flow card
            val netFlow = state.monthlyIncome - state.monthlyExpense
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (netFlow >= 0) IncomeGreen.copy(alpha = 0.1f) else ExpenseRose.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Net Cash Flow", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            (if (netFlow >= 0) "+" else "") + CurrencyFormatter.format(netFlow),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (netFlow >= 0) IncomeGreen else ExpenseRose
                        )
                    }
                    Icon(
                        if (netFlow >= 0) Icons.Rounded.TrendingUp else Icons.Rounded.TrendingDown,
                        contentDescription = null,
                        tint = if (netFlow >= 0) IncomeGreen else ExpenseRose,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Income vs Expense Bar Chart (visual representation)
            Text("Income vs Expense", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    val maxAmount = maxOf(state.monthlyIncome, state.monthlyExpense, 1.0)

                    // Income bar
                    Text("Income", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth((state.monthlyIncome / maxAmount).toFloat().coerceIn(0f, 1f))
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.horizontalGradient(listOf(IncomeGreen, IncomeGreenLight)))
                        ) {
                            Text(
                                CurrencyFormatter.format(state.monthlyIncome),
                                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White, fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Expense bar
                    Text("Expense", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth((state.monthlyExpense / maxAmount).toFloat().coerceIn(0f, 1f))
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.horizontalGradient(listOf(ExpenseRose, ExpenseRoseLight)))
                        ) {
                            Text(
                                CurrencyFormatter.format(state.monthlyExpense),
                                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White, fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Category Breakdown
            Text("Category Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (state.categoryBreakdown.isEmpty()) {
                        Text(
                            "No expense data this month",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val totalExpense = state.categoryBreakdown.sumOf { it.second }
                        state.categoryBreakdown.take(8).forEach { (categoryId, amount) ->
                            val category = state.categories[categoryId]
                            val percentage = if (totalExpense > 0) (amount / totalExpense * 100) else 0.0
                            val color = category?.color?.let { Color(it) } ?: Primary

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(shape = RoundedCornerShape(4.dp), color = color, modifier = Modifier.size(12.dp)) {}
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    category?.name ?: "Unknown",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "${percentage.toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    CurrencyFormatter.format(amount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            FinTrackProgressBar(
                                progress = (percentage / 100).toFloat(),
                                gradientColors = listOf(color, color.copy(alpha = 0.6f)),
                                showLabel = false, height = 6
                            )
                        }
                    }
                }
            }

            // Daily Spending
            Text("Daily Spending", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (state.dailySpending.isEmpty()) {
                        Text("No spending data yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        val maxDaily = state.dailySpending.maxOfOrNull { it.amount } ?: 1.0
                        Row(
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            state.dailySpending.forEach { daily ->
                                val heightFraction = (daily.amount / maxDaily).toFloat().coerceIn(0.05f, 1f)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Primary, Primary.copy(alpha = 0.3f))
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
