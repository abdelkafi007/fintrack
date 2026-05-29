package com.fintrack.domain.usecase

import com.fintrack.domain.model.*
import com.fintrack.domain.repository.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

// ====== Transaction Use Cases ======

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> = repository.getAllTransactions()
}

class GetTransactionsByDateRangeUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> =
        repository.getTransactionsByDateRange(startDate, endDate)
}

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Long> {
        if (transaction.amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than 0"))
        }

        // Update account balance
        val account = accountRepository.getAccountByIdSync(transaction.accountId)
            ?: return Result.failure(IllegalArgumentException("Account not found"))

        val newBalance = when (transaction.type) {
            TransactionType.INCOME -> account.balance + transaction.amount
            TransactionType.EXPENSE -> account.balance - transaction.amount
            TransactionType.TRANSFER -> account.balance - transaction.amount
        }

        accountRepository.updateAccount(account.copy(balance = newBalance))

        // For transfers, also update the destination account
        if (transaction.type == TransactionType.TRANSFER && transaction.toAccountId != null) {
            val toAccount = accountRepository.getAccountByIdSync(transaction.toAccountId)
            if (toAccount != null) {
                accountRepository.updateAccount(toAccount.copy(balance = toAccount.balance + transaction.amount))
            }
        }

        val id = transactionRepository.addTransaction(
            transaction.copy(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
        return Result.success(id)
    }
}

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        if (transaction.amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than 0"))
        }
        repository.updateTransaction(transaction.copy(updatedAt = LocalDateTime.now()))
        return Result.success(Unit)
    }
}

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        // Reverse the balance change
        val account = accountRepository.getAccountByIdSync(transaction.accountId)
        if (account != null) {
            val restoredBalance = when (transaction.type) {
                TransactionType.INCOME -> account.balance - transaction.amount
                TransactionType.EXPENSE -> account.balance + transaction.amount
                TransactionType.TRANSFER -> account.balance + transaction.amount
            }
            accountRepository.updateAccount(account.copy(balance = restoredBalance))
        }

        // Reverse transfer destination
        if (transaction.type == TransactionType.TRANSFER && transaction.toAccountId != null) {
            val toAccount = accountRepository.getAccountByIdSync(transaction.toAccountId)
            if (toAccount != null) {
                accountRepository.updateAccount(toAccount.copy(balance = toAccount.balance - transaction.amount))
            }
        }

        transactionRepository.deleteTransaction(transaction)
        return Result.success(Unit)
    }
}

class SearchTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(query: String): Flow<List<Transaction>> = repository.searchTransactions(query)
}

// ====== Category Use Cases ======

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.getAllCategories()
    fun byType(type: CategoryType): Flow<List<Category>> = repository.getCategoriesByType(type)
}

class CreateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Result<Long> {
        if (category.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Category name cannot be empty"))
        }
        val id = repository.addCategory(category)
        return Result.success(id)
    }
}

class UpdateCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) = repository.updateCategory(category)
}

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) = repository.deleteCategory(category)
}

// ====== Account Use Cases ======

class GetAccountsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(): Flow<List<Account>> = repository.getActiveAccounts()
    fun all(): Flow<List<Account>> = repository.getAllAccounts()
}

class AddAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Result<Long> {
        if (account.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Account name cannot be empty"))
        }
        val id = repository.addAccount(account)
        return Result.success(id)
    }
}

class UpdateAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(account: Account) = repository.updateAccount(account)
}

class TransferBetweenAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(fromAccountId: Long, toAccountId: Long, amount: Double): Result<Unit> {
        if (amount <= 0) return Result.failure(IllegalArgumentException("Amount must be greater than 0"))

        val fromAccount = accountRepository.getAccountByIdSync(fromAccountId)
            ?: return Result.failure(IllegalArgumentException("Source account not found"))
        val toAccount = accountRepository.getAccountByIdSync(toAccountId)
            ?: return Result.failure(IllegalArgumentException("Destination account not found"))

        if (fromAccount.balance < amount) {
            return Result.failure(IllegalArgumentException("Insufficient balance"))
        }

        // Update balances
        accountRepository.updateAccount(fromAccount.copy(balance = fromAccount.balance - amount))
        accountRepository.updateAccount(toAccount.copy(balance = toAccount.balance + amount))

        // Create transfer transaction record
        transactionRepository.addTransaction(
            Transaction(
                amount = amount,
                type = TransactionType.TRANSFER,
                categoryId = 0, // No category for transfers
                accountId = fromAccountId,
                toAccountId = toAccountId,
                date = LocalDate.now(),
                notes = "Transfer: ${fromAccount.name} → ${toAccount.name}",
                currencyCode = fromAccount.currencyCode
            )
        )

        return Result.success(Unit)
    }
}

class GetNetWorthUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(): Flow<Double> = repository.getTotalBalance()
}

// ====== Budget Use Cases ======

class GetBudgetsUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    operator fun invoke(): Flow<List<Budget>> = repository.getAllBudgets()
}

class SetBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget): Result<Long> {
        if (budget.amount <= 0) {
            return Result.failure(IllegalArgumentException("Budget amount must be greater than 0"))
        }
        val id = repository.addBudget(budget)
        return Result.success(id)
    }
}

class UpdateBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget) = repository.updateBudget(budget)
}

class DeleteBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget) = repository.deleteBudget(budget)
}

// ====== Goal Use Cases ======

class GetGoalsUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    operator fun invoke(): Flow<List<Goal>> = repository.getAllGoals()
    fun active(): Flow<List<Goal>> = repository.getActiveGoals()
    fun completed(): Flow<List<Goal>> = repository.getCompletedGoals()
}

class CreateGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal): Result<Long> {
        if (goal.name.isBlank()) return Result.failure(IllegalArgumentException("Goal name cannot be empty"))
        if (goal.targetAmount <= 0) return Result.failure(IllegalArgumentException("Target amount must be greater than 0"))
        val id = repository.addGoal(goal)
        return Result.success(id)
    }
}

class AllocateToGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goalId: Long, amount: Double): Result<Unit> {
        if (amount <= 0) return Result.failure(IllegalArgumentException("Amount must be greater than 0"))

        val goal = repository.getGoalById(goalId)
        var currentGoal: Goal? = null
        goal.collect { currentGoal = it; return@collect }

        currentGoal?.let {
            val newAmount = it.currentAmount + amount
            val isCompleted = newAmount >= it.targetAmount
            repository.updateGoal(it.copy(currentAmount = newAmount, isCompleted = isCompleted))
            return Result.success(Unit)
        }

        return Result.failure(IllegalArgumentException("Goal not found"))
    }
}

class UpdateGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal) = repository.updateGoal(goal)
}

class DeleteGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(goal: Goal) = repository.deleteGoal(goal)
}

// ====== Analytics Use Cases ======

class GetMonthlyAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<Pair<Double, Double>> =
        analyticsRepository.getMonthlyTotals(startDate, endDate)
}

class GetCategoryBreakdownUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<List<Pair<Long, Double>>> =
        analyticsRepository.getCategoryTotals(startDate, endDate)
}

class GetDailySpendingTrendUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<List<DailySpending>> =
        analyticsRepository.getDailySpending(startDate, endDate)
}
