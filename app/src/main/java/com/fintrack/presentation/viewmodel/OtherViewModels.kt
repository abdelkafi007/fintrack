package com.fintrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fintrack.core.utils.DateRangeHelper
import com.fintrack.domain.model.*
import com.fintrack.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

// ======== Budgets ViewModel ========

data class BudgetsUiState(
    val budgets: List<Budget> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetsUiState())
    val uiState: StateFlow<BudgetsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getBudgetsUseCase().collect { budgets ->
                _uiState.update { it.copy(budgets = budgets, isLoading = false) }
            }
        }
        viewModelScope.launch {
            getCategoriesUseCase().collect { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch { deleteBudgetUseCase(budget) }
    }
}

// Add Budget
data class AddBudgetUiState(
    val categoryId: Long? = null,
    val amount: String = "",
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val rollover: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String? = null,
    val savedSuccessfully: Boolean = false
)

@HiltViewModel
class AddBudgetViewModel @Inject constructor(
    private val setBudgetUseCase: SetBudgetUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBudgetUiState())
    val uiState: StateFlow<AddBudgetUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCategoriesUseCase.byType(CategoryType.EXPENSE).collect { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
    }

    fun onCategorySelect(id: Long) { _uiState.update { it.copy(categoryId = id, error = null) } }
    fun onAmountChange(v: String) { _uiState.update { it.copy(amount = v, error = null) } }
    fun onPeriodChange(p: BudgetPeriod) { _uiState.update { it.copy(period = p) } }
    fun onRolloverToggle(r: Boolean) { _uiState.update { it.copy(rollover = r) } }

    fun save() {
        val s = _uiState.value
        val amount = s.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) { _uiState.update { it.copy(error = "Enter valid amount") }; return }
        if (s.categoryId == null) { _uiState.update { it.copy(error = "Select a category") }; return }

        viewModelScope.launch {
            setBudgetUseCase(Budget(
                categoryId = s.categoryId, amount = amount, period = s.period,
                startDate = LocalDate.now().withDayOfMonth(1), rollover = s.rollover
            ))
            _uiState.update { it.copy(savedSuccessfully = true) }
        }
    }
}

// ======== Accounts ViewModel ========

data class AccountsUiState(
    val accounts: List<Account> = emptyList(),
    val totalBalance: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getNetWorthUseCase: GetNetWorthUseCase,
    private val transferBetweenAccountsUseCase: TransferBetweenAccountsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountsUiState())
    val uiState: StateFlow<AccountsUiState> = _uiState.asStateFlow()
    private val _snackbar = MutableSharedFlow<String>()
    val snackbar: SharedFlow<String> = _snackbar.asSharedFlow()

    init {
        viewModelScope.launch {
            getAccountsUseCase().collect { accs ->
                _uiState.update { it.copy(accounts = accs, isLoading = false) }
            }
        }
        viewModelScope.launch {
            getNetWorthUseCase().collect { bal ->
                _uiState.update { it.copy(totalBalance = bal) }
            }
        }
    }

    fun transfer(fromId: Long, toId: Long, amount: Double) {
        viewModelScope.launch {
            transferBetweenAccountsUseCase(fromId, toId, amount).fold(
                onSuccess = { _snackbar.emit("Transfer successful") },
                onFailure = { _snackbar.emit(it.message ?: "Transfer failed") }
            )
        }
    }
}

// Add Account
data class AddAccountUiState(
    val name: String = "",
    val type: AccountType = AccountType.BANK,
    val balance: String = "0",
    val currencyCode: String = "USD",
    val error: String? = null,
    val savedSuccessfully: Boolean = false
)

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val addAccountUseCase: AddAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddAccountUiState())
    val uiState: StateFlow<AddAccountUiState> = _uiState.asStateFlow()

    fun onNameChange(v: String) { _uiState.update { it.copy(name = v, error = null) } }
    fun onTypeChange(t: AccountType) { _uiState.update { it.copy(type = t) } }
    fun onBalanceChange(v: String) { _uiState.update { it.copy(balance = v, error = null) } }

    fun save() {
        val s = _uiState.value
        if (s.name.isBlank()) { _uiState.update { it.copy(error = "Enter account name") }; return }

        val iconAndColor = when (s.type) {
            AccountType.CASH -> Pair("wallet", 0xFF10B981L)
            AccountType.BANK -> Pair("account_balance", 0xFF3B82F6L)
            AccountType.CREDIT_CARD -> Pair("credit_card", 0xFF8B5CF6L)
            AccountType.SAVINGS -> Pair("savings", 0xFFF59E0BL)
        }

        viewModelScope.launch {
            addAccountUseCase(Account(
                name = s.name, type = s.type,
                balance = s.balance.toDoubleOrNull() ?: 0.0,
                currencyCode = s.currencyCode,
                icon = iconAndColor.first, color = iconAndColor.second
            ))
            _uiState.update { it.copy(savedSuccessfully = true) }
        }
    }
}

// ======== Goals ViewModel ========

data class GoalsUiState(
    val activeGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val allocateToGoalUseCase: AllocateToGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getGoalsUseCase.active().collect { goals ->
                _uiState.update { it.copy(activeGoals = goals, isLoading = false) }
            }
        }
        viewModelScope.launch {
            getGoalsUseCase.completed().collect { goals ->
                _uiState.update { it.copy(completedGoals = goals) }
            }
        }
    }

    fun allocate(goalId: Long, amount: Double) {
        viewModelScope.launch { allocateToGoalUseCase(goalId, amount) }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch { deleteGoalUseCase(goal) }
    }
}

// Add Goal
data class AddGoalUiState(
    val name: String = "",
    val targetAmount: String = "",
    val deadline: LocalDate? = null,
    val error: String? = null,
    val savedSuccessfully: Boolean = false
)

@HiltViewModel
class AddGoalViewModel @Inject constructor(
    private val createGoalUseCase: CreateGoalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddGoalUiState())
    val uiState: StateFlow<AddGoalUiState> = _uiState.asStateFlow()

    fun onNameChange(v: String) { _uiState.update { it.copy(name = v, error = null) } }
    fun onTargetChange(v: String) { _uiState.update { it.copy(targetAmount = v, error = null) } }
    fun onDeadlineChange(d: LocalDate?) { _uiState.update { it.copy(deadline = d) } }

    fun save() {
        val s = _uiState.value
        if (s.name.isBlank()) { _uiState.update { it.copy(error = "Enter goal name") }; return }
        val target = s.targetAmount.toDoubleOrNull()
        if (target == null || target <= 0) { _uiState.update { it.copy(error = "Enter valid amount") }; return }

        viewModelScope.launch {
            createGoalUseCase(Goal(
                name = s.name, targetAmount = target, deadline = s.deadline,
                icon = "flag", color = 0xFF6C63FFL
            ))
            _uiState.update { it.copy(savedSuccessfully = true) }
        }
    }
}

// ======== Analytics ViewModel ========

data class AnalyticsUiState(
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val categoryBreakdown: List<Pair<Long, Double>> = emptyList(),
    val dailySpending: List<DailySpending> = emptyList(),
    val categories: Map<Long, Category> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getMonthlyAnalyticsUseCase: GetMonthlyAnalyticsUseCase,
    private val getCategoryBreakdownUseCase: GetCategoryBreakdownUseCase,
    private val getDailySpendingTrendUseCase: GetDailySpendingTrendUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentMonth()
    }

    private fun loadCurrentMonth() {
        val (start, end) = DateRangeHelper.getCurrentMonthRange()
        viewModelScope.launch {
            getMonthlyAnalyticsUseCase(start, end).collect { (income, expense) ->
                _uiState.update { it.copy(monthlyIncome = income, monthlyExpense = expense, isLoading = false) }
            }
        }
        viewModelScope.launch {
            getCategoryBreakdownUseCase(start, end).collect { breakdown ->
                _uiState.update { it.copy(categoryBreakdown = breakdown) }
            }
        }
        viewModelScope.launch {
            getDailySpendingTrendUseCase(start, end).collect { daily ->
                _uiState.update { it.copy(dailySpending = daily) }
            }
        }
        viewModelScope.launch {
            getCategoriesUseCase().collect { cats ->
                _uiState.update { it.copy(categories = cats.associateBy { it.id }) }
            }
        }
    }
}

// ======== Settings ViewModel ========

data class SettingsUiState(
    val isDarkTheme: Boolean? = null, // null = system
    val currency: String = "USD",
    val isAppLockEnabled: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onThemeChange(isDark: Boolean?) { _uiState.update { it.copy(isDarkTheme = isDark) } }
    fun onCurrencyChange(code: String) { _uiState.update { it.copy(currency = code) } }
    fun onAppLockToggle(enabled: Boolean) { _uiState.update { it.copy(isAppLockEnabled = enabled) } }
}
