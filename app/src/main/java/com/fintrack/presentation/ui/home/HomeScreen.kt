package com.fintrack.presentation.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fintrack.core.utils.CurrencyFormatter
import com.fintrack.core.utils.DateFormatter
import com.fintrack.domain.model.TransactionType
import com.fintrack.presentation.ui.components.*
import com.fintrack.presentation.ui.theme.*
import com.fintrack.presentation.viewmodel.HomeViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToAddTransaction: () -> Unit = {},
    onNavigateToAccounts: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = Primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            // Shimmer loading
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 40.dp)
                Spacer(modifier = Modifier.height(20.dp))
                ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 140.dp, cornerRadius = 24.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShimmerBox(modifier = Modifier.weight(1f), height = 80.dp)
                    ShimmerBox(modifier = Modifier.weight(1f), height = 80.dp)
                }
                Spacer(modifier = Modifier.height(24.dp))
                ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 20.dp)
                Spacer(modifier = Modifier.height(12.dp))
                repeat(3) {
                    ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 64.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Greeting
                item {
                    Column {
                        Text(
                            text = getGreeting(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = DateFormatter.formatFull(LocalDate.now()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Total Balance Card
                item {
                    GradientCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Total Balance",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = CurrencyFormatter.format(state.totalBalance),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Income / Expense Mini Cards
                item {
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
                }

                // Accounts
                if (state.accounts.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Accounts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            TextButton(onClick = onNavigateToAccounts) {
                                Text("See All")
                            }
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            state.accounts.forEach { account ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(account.color).copy(alpha = 0.12f),
                                    modifier = Modifier.width(150.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Icon(
                                            imageVector = when (account.type.name) {
                                                "CASH" -> Icons.Rounded.Wallet
                                                "BANK" -> Icons.Rounded.AccountBalance
                                                "CREDIT_CARD" -> Icons.Rounded.CreditCard
                                                else -> Icons.Rounded.Savings
                                            },
                                            contentDescription = null,
                                            tint = Color(account.color),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            account.name,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            CurrencyFormatter.format(account.balance),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Recent Transactions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        TextButton(onClick = onNavigateToTransactions) { Text("See All") }
                    }
                }

                if (state.recentTransactions.isEmpty()) {
                    item {
                        EmptyState(
                            icon = {
                                Icon(
                                    Icons.Rounded.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            },
                            title = "No transactions yet",
                            description = "Tap + to add your first transaction",
                            actionLabel = "Add Transaction",
                            onAction = onNavigateToAddTransaction
                        )
                    }
                } else {
                    items(state.recentTransactions, key = { it.id }) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            categoryName = state.categories[transaction.categoryId]?.name ?: "Unknown",
                            categoryColor = state.categories[transaction.categoryId]?.color?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Bottom spacing for FAB
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: com.fintrack.domain.model.Transaction,
    categoryName: String,
    categoryColor: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon circle
            Surface(
                shape = CircleShape,
                color = categoryColor.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (transaction.type) {
                            TransactionType.INCOME -> Icons.Rounded.ArrowDownward
                            TransactionType.EXPENSE -> Icons.Rounded.ArrowUpward
                            TransactionType.TRANSFER -> Icons.Rounded.SwapHoriz
                        },
                        contentDescription = null,
                        tint = categoryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.notes.ifBlank { categoryName },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = "$categoryName · ${DateFormatter.getRelativeDate(transaction.date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AmountText(
                amount = transaction.amount,
                type = transaction.type,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

private fun getGreeting(): String {
    val hour = java.time.LocalTime.now().hour
    return when {
        hour < 12 -> "Good morning ☀️"
        hour < 17 -> "Good afternoon 👋"
        else -> "Good evening 🌙"
    }
}
