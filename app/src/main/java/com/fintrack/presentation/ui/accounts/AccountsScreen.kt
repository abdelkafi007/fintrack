package com.fintrack.presentation.ui.accounts

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
import com.fintrack.domain.model.Account
import com.fintrack.domain.model.AccountType
import com.fintrack.presentation.ui.components.*
import com.fintrack.presentation.ui.theme.*
import com.fintrack.presentation.viewmodel.AccountsViewModel
import com.fintrack.presentation.viewmodel.AddAccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onNavigateToAddAccount: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: AccountsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accounts", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Rounded.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddAccount, containerColor = Primary, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Account")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Net worth card
            item {
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    gradientColors = listOf(Color(0xFF6C63FF), Color(0xFF14B8A6))
                ) {
                    Column {
                        Text("Net Worth", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            CurrencyFormatter.format(state.totalBalance),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold, color = Color.White
                        )
                    }
                }
            }

            items(state.accounts, key = { it.id }) { account ->
                AccountCard(account = account)
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }
}

@Composable
private fun AccountCard(account: Account) {
    val icon = when (account.type) {
        AccountType.CASH -> Icons.Rounded.Wallet
        AccountType.BANK -> Icons.Rounded.AccountBalance
        AccountType.CREDIT_CARD -> Icons.Rounded.CreditCard
        AccountType.SAVINGS -> Icons.Rounded.Savings
    }

    FinTrackCard(modifier = Modifier.fillMaxWidth(), accentColor = Color(account.color)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = Color(account.color).copy(alpha = 0.15f), modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color(account.color), modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(account.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(account.type.name.replace("_", " "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                CurrencyFormatter.format(account.balance),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (account.balance >= 0) IncomeGreen else ExpenseRose
            )
        }
    }
}

// ======== Add Account Screen ========

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AddAccountViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.savedSuccessfully) { if (state.savedSuccessfully) onNavigateBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Account", fontWeight = FontWeight.Bold) },
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
                modifier = Modifier.fillMaxWidth(), label = { Text("Account Name") },
                shape = RoundedCornerShape(16.dp), singleLine = true,
                isError = state.error != null
            )

            Text("Account Type", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AccountType.entries.forEach { type ->
                    val label = type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
                    FilterChip(
                        selected = state.type == type, onClick = { viewModel.onTypeChange(type) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = state.balance, onValueChange = viewModel::onBalanceChange,
                modifier = Modifier.fillMaxWidth(), label = { Text("Initial Balance") },
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
                Text("Save Account", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
