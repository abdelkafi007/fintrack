package com.fintrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.domain.model.*
import com.fintrack.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: Map<Long, Category> = emptyMap(),
    val accounts: Map<Long, Account> = emptyMap(),
    val searchQuery: String = "",
    val filterType: TransactionType? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val searchTransactionsUseCase: SearchTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getAccountsUseCase: GetAccountsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    private var lastDeletedTransaction: Transaction? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getTransactionsUseCase().collect { transactions ->
                _uiState.update { it.copy(transactions = transactions, isLoading = false) }
            }
        }
        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(categories = categories.associateBy { c -> c.id }) }
            }
        }
        viewModelScope.launch {
            getAccountsUseCase().collect { accounts ->
                _uiState.update { it.copy(accounts = accounts.associateBy { a -> a.id }) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadData()
        } else {
            viewModelScope.launch {
                searchTransactionsUseCase(query).collect { results ->
                    _uiState.update { it.copy(transactions = results) }
                }
            }
        }
    }

    fun onFilterChange(type: TransactionType?) {
        _uiState.update { it.copy(filterType = type) }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            lastDeletedTransaction = transaction
            deleteTransactionUseCase(transaction)
            _snackbarMessage.emit("Transaction deleted")
        }
    }

    fun undoDelete() {
        lastDeletedTransaction?.let { transaction ->
            viewModelScope.launch {
                // Re-add would need AddTransactionUseCase - simplified here
                lastDeletedTransaction = null
            }
        }
    }

    val filteredTransactions: Flow<List<Transaction>> = _uiState.map { state ->
        val filtered = if (state.filterType != null) {
            state.transactions.filter { it.type == state.filterType }
        } else {
            state.transactions
        }
        filtered
    }
}

// ======== Add/Edit Transaction ========

data class AddTransactionUiState(
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryId: Long? = null,
    val accountId: Long? = null,
    val toAccountId: Long? = null,
    val date: LocalDate = LocalDate.now(),
    val notes: String = "",
    val isRecurring: Boolean = false,
    val recurringPeriod: TransactionPeriod? = null,
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val isSaving: Boolean = false,
    val error: String? = null,
    val savedSuccessfully: Boolean = false
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getAccountsUseCase: GetAccountsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            getAccountsUseCase().collect { accounts ->
                _uiState.update {
                    it.copy(
                        accounts = accounts,
                        accountId = accounts.firstOrNull()?.id
                    )
                }
            }
        }
    }

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amount = value, error = null) }
    }

    fun onTypeChange(type: TransactionType) {
        _uiState.update { it.copy(type = type) }
    }

    fun onCategorySelect(categoryId: Long) {
        _uiState.update { it.copy(categoryId = categoryId, error = null) }
    }

    fun onAccountSelect(accountId: Long) {
        _uiState.update { it.copy(accountId = accountId) }
    }

    fun onToAccountSelect(accountId: Long) {
        _uiState.update { it.copy(toAccountId = accountId) }
    }

    fun onDateChange(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onRecurringToggle(isRecurring: Boolean) {
        _uiState.update { it.copy(isRecurring = isRecurring) }
    }

    fun onRecurringPeriodChange(period: TransactionPeriod) {
        _uiState.update { it.copy(recurringPeriod = period) }
    }

    fun saveTransaction() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(error = "Please enter a valid amount") }
            return
        }
        if (state.categoryId == null && state.type != TransactionType.TRANSFER) {
            _uiState.update { it.copy(error = "Please select a category") }
            return
        }
        if (state.accountId == null) {
            _uiState.update { it.copy(error = "Please select an account") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = addTransactionUseCase(
                Transaction(
                    amount = amount,
                    type = state.type,
                    categoryId = state.categoryId ?: 0,
                    accountId = state.accountId,
                    toAccountId = state.toAccountId,
                    date = state.date,
                    notes = state.notes,
                    isRecurring = state.isRecurring,
                    recurringPeriod = if (state.isRecurring) state.recurringPeriod else null
                )
            )
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isSaving = false, error = e.message) }
                }
            )
        }
    }
}
