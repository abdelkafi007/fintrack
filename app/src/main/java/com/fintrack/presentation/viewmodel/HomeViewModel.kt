package com.fintrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.core.utils.DateRangeHelper
import com.fintrack.domain.model.*
import com.fintrack.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val accounts: List<Account> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val categories: Map<Long, Category> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getNetWorthUseCase: GetNetWorthUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getMonthlyAnalyticsUseCase: GetMonthlyAnalyticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val (monthStart, monthEnd) = DateRangeHelper.getCurrentMonthRange()

        // Load net worth
        viewModelScope.launch {
            getNetWorthUseCase().collect { balance ->
                _uiState.update { it.copy(totalBalance = balance) }
            }
        }

        // Load accounts
        viewModelScope.launch {
            getAccountsUseCase().collect { accounts ->
                _uiState.update { it.copy(accounts = accounts) }
            }
        }

        // Load recent transactions
        viewModelScope.launch {
            getTransactionsUseCase().collect { transactions ->
                _uiState.update {
                    it.copy(
                        recentTransactions = transactions.take(5),
                        isLoading = false
                    )
                }
            }
        }

        // Load categories for display
        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update {
                    it.copy(categories = categories.associateBy { c -> c.id })
                }
            }
        }

        // Load monthly totals
        viewModelScope.launch {
            getMonthlyAnalyticsUseCase(monthStart, monthEnd).collect { (income, expense) ->
                _uiState.update {
                    it.copy(monthlyIncome = income, monthlyExpense = expense)
                }
            }
        }
    }
}
