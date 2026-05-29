package com.fintrack.presentation.ui.budgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fintrack.core.utils.CurrencyFormatter
import com.fintrack.domain.model.Budget
import com.fintrack.domain.model.BudgetPeriod
import com.fintrack.presentation.ui.components.*
import com.fintrack.presentation.ui.theme.*
import com.fintrack.presentation.viewmodel.AddBudgetViewModel
import com.fintrack.presentation.viewmodel.BudgetsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(
    onNavigateToAddBudget: () -> Unit = {},
    viewModel: BudgetsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budgets", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddBudget,
                containerColor = Primary,
                contentColor = Color.White,
                shape = CircleShape
            ) { Icon(Icons.Rounded.Add, contentDescription = "Add Budget") }
        }
    ) { padding ->
        if (state.budgets.isEmpty() && !state.isLoading) {
            EmptyState(
                modifier = Modifier.fillMaxSize().padding(padding),
                icon = {
                    Icon(Icons.Rounded.PieChart, contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                },
                title = "No budgets set",
                description = "Create budgets to track spending per category",
                actionLabel = "Add Budget",
                onAction = onNavigateToAddBudget
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Monthly overview
                item {
                    val totalSpent = state.budgets.sumOf { it.spent }
                    val totalBudget = state.budgets.sumOf { it.amount }
                    GradientCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("Monthly Overview", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "${CurrencyFormatter.format(totalSpent)} of ${CurrencyFormatter.format(totalBudget)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold, color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            FinTrackProgressBar(
                                progress = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f,
                                gradientColors = listOf(Color.White.copy(alpha = 0.8f), Color.White),
                                showLabel = false, height = 8
                            )
                        }
                    }
                }

                items(state.budgets, key = { it.id }) { budget ->
                    BudgetCard(budget = budget)
                }

                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }
}

@Composable
private fun BudgetCard(budget: Budget) {
    FinTrackCard(
        modifier = Modifier.fillMaxWidth(),
        accentColor = Color(budget.categoryColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(budget.categoryColor).copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Category, contentDescription = null,
                        tint = Color(budget.categoryColor), modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(budget.categoryName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(
                    "${CurrencyFormatter.format(budget.spent)} / ${CurrencyFormatter.format(budget.amount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (budget.isOverBudget) {
                Icon(Icons.Rounded.Warning, contentDescription = "Over budget",
                    tint = ExpenseRose, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        FinTrackProgressBar(
            progress = budget.progress,
            isOverBudget = budget.isOverBudget,
            showLabel = true
        )
    }
}

// ======== Add Budget Screen ========

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddBudgetScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AddBudgetViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.savedSuccessfully) { if (state.savedSuccessfully) onNavigateBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Budget", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Rounded.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category chips
            Text("Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.categories.forEach { cat ->
                    FilterChip(
                        selected = state.categoryId == cat.id,
                        onClick = { viewModel.onCategorySelect(cat.id) },
                        label = { Text(cat.name) },
                        leadingIcon = {
                            Box(modifier = Modifier.size(8.dp).padding(0.dp)) {
                                Surface(shape = CircleShape, color = Color(cat.color), modifier = Modifier.size(8.dp)) {}
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = state.amount, onValueChange = viewModel::onAmountChange,
                modifier = Modifier.fillMaxWidth(), label = { Text("Budget Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp), singleLine = true,
                leadingIcon = { Text("$", fontWeight = FontWeight.Bold) }
            )

            // Period
            Text("Period", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BudgetPeriod.entries.forEach { period ->
                    FilterChip(
                        selected = state.period == period,
                        onClick = { viewModel.onPeriodChange(period) },
                        label = { Text(period.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Rollover
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Roll over unused budget", modifier = Modifier.weight(1f))
                Switch(checked = state.rollover, onCheckedChange = viewModel::onRolloverToggle)
            }

            state.error?.let { Text(it, color = ExpenseRose, style = MaterialTheme.typography.bodySmall) }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Budget", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
