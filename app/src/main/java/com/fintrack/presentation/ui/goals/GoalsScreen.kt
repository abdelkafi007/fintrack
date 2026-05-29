package com.fintrack.presentation.ui.goals

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
import com.fintrack.core.utils.DateFormatter
import com.fintrack.domain.model.Goal
import com.fintrack.presentation.ui.components.*
import com.fintrack.presentation.ui.theme.*
import com.fintrack.presentation.viewmodel.AddGoalViewModel
import com.fintrack.presentation.viewmodel.GoalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigateToAddGoal: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Savings Goals", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Rounded.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddGoal, containerColor = Primary, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Goal")
            }
        }
    ) { padding ->
        if (state.activeGoals.isEmpty() && state.completedGoals.isEmpty() && !state.isLoading) {
            EmptyState(
                modifier = Modifier.fillMaxSize().padding(padding),
                icon = { Icon(Icons.Rounded.Flag, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)) },
                title = "No savings goals",
                description = "Set goals to save toward something special",
                actionLabel = "Add Goal", onAction = onNavigateToAddGoal
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.activeGoals.isNotEmpty()) {
                    item { Text("Active Goals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
                    items(state.activeGoals, key = { it.id }) { goal -> GoalCard(goal = goal) }
                }
                if (state.completedGoals.isNotEmpty()) {
                    item { Text("Completed 🎉", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp)) }
                    items(state.completedGoals, key = { it.id }) { goal -> GoalCard(goal = goal) }
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }
}

@Composable
private fun GoalCard(goal: Goal) {
    FinTrackCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                progress = goal.progress,
                color = Color(goal.color),
                modifier = Modifier.size(80.dp)
            ) {
                Text(
                    "${(goal.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(goal.color)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(goal.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${CurrencyFormatter.format(goal.currentAmount)} / ${CurrencyFormatter.format(goal.targetAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                goal.deadline?.let { deadline ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Due: ${DateFormatter.formatShort(deadline)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (goal.isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = IncomeGreen.copy(alpha = 0.15f)) {
                        Text("Completed!", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall, color = IncomeGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ======== Add Goal Screen ========

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AddGoalViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.savedSuccessfully) { if (state.savedSuccessfully) onNavigateBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Goal", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Rounded.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.name, onValueChange = viewModel::onNameChange,
                modifier = Modifier.fillMaxWidth(), label = { Text("Goal Name") },
                placeholder = { Text("e.g., Emergency Fund") },
                shape = RoundedCornerShape(16.dp), singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Flag, null) }
            )

            OutlinedTextField(
                value = state.targetAmount, onValueChange = viewModel::onTargetChange,
                modifier = Modifier.fillMaxWidth(), label = { Text("Target Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp), singleLine = true,
                leadingIcon = { Text("$", fontWeight = FontWeight.Bold) }
            )

            state.error?.let { Text(it, color = ExpenseRose, style = MaterialTheme.typography.bodySmall) }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::save, modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Goal", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
