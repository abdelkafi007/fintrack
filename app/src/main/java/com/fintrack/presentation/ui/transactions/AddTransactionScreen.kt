package com.fintrack.presentation.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fintrack.domain.model.CategoryType
import com.fintrack.domain.model.TransactionType
import com.fintrack.presentation.ui.theme.*
import com.fintrack.presentation.viewmodel.AddTransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount Input
            OutlinedTextField(
                value = state.amount,
                onValueChange = viewModel::onAmountChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Amount") },
                leadingIcon = { Text("$", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                ),
                singleLine = true,
                isError = state.error != null && state.amount.isBlank()
            )

            // Type Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactionType.entries.forEach { type ->
                    val selected = state.type == type
                    val color = when (type) {
                        TransactionType.INCOME -> IncomeGreen
                        TransactionType.EXPENSE -> ExpenseRose
                        TransactionType.TRANSFER -> TransferBlue
                    }
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onTypeChange(type) },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Category Selection
            if (state.type != TransactionType.TRANSFER) {
                Text("Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                val filteredCategories = state.categories.filter {
                    when (state.type) {
                        TransactionType.INCOME -> it.type == CategoryType.INCOME
                        TransactionType.EXPENSE -> it.type == CategoryType.EXPENSE
                        else -> true
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCategories) { category ->
                        val isSelected = state.categoryId == category.id
                        Surface(
                            onClick = { viewModel.onCategorySelect(category.id) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(category.color).copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceContainerHigh,
                            border = if (isSelected) ButtonDefaults.outlinedButtonBorder(enabled = true) else null
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(category.color).copy(alpha = 0.15f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Rounded.Category,
                                            contentDescription = null,
                                            tint = Color(category.color),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    category.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Account selector
            if (state.accounts.isNotEmpty()) {
                Text("Account", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.accounts.forEach { account ->
                        FilterChip(
                            selected = state.accountId == account.id,
                            onClick = { viewModel.onAccountSelect(account.id) },
                            label = { Text(account.name) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (state.type == TransactionType.TRANSFER) {
                    Text("To Account", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.accounts.filter { it.id != state.accountId }.forEach { account ->
                            FilterChip(
                                selected = state.toAccountId == account.id,
                                onClick = { viewModel.onToAccountSelect(account.id) },
                                label = { Text(account.name) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // Notes
            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notes (optional)") },
                leadingIcon = { Icon(Icons.Rounded.Notes, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                maxLines = 2
            )

            // Error
            state.error?.let { error ->
                Text(
                    text = error,
                    color = ExpenseRose,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Save button
            Button(
                onClick = viewModel::saveTransaction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isSaving,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Rounded.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Transaction", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
