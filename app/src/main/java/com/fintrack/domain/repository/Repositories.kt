package com.fintrack.domain.repository

import com.fintrack.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionById(id: Long): Flow<Transaction?>
    fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>>
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>
    fun getTransactionsByAccount(accountId: Long): Flow<List<Transaction>>
    fun searchTransactions(query: String): Flow<List<Transaction>>
    suspend fun addTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
}

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>
    fun getCategoryById(id: Long): Flow<Category?>
    suspend fun addCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
}

interface AccountRepository {
    fun getAllAccounts(): Flow<List<Account>>
    fun getActiveAccounts(): Flow<List<Account>>
    fun getAccountById(id: Long): Flow<Account?>
    suspend fun getAccountByIdSync(id: Long): Account?
    fun getTotalBalance(): Flow<Double>
    suspend fun addAccount(account: Account): Long
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(account: Account)
}

interface BudgetRepository {
    fun getAllBudgets(): Flow<List<Budget>>
    fun getBudgetById(id: Long): Flow<Budget?>
    fun getBudgetByCategory(categoryId: Long): Flow<Budget?>
    suspend fun addBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}

interface GoalRepository {
    fun getAllGoals(): Flow<List<Goal>>
    fun getActiveGoals(): Flow<List<Goal>>
    fun getCompletedGoals(): Flow<List<Goal>>
    fun getGoalById(id: Long): Flow<Goal?>
    suspend fun addGoal(goal: Goal): Long
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
}

interface AnalyticsRepository {
    fun getMonthlyTotals(startDate: LocalDate, endDate: LocalDate): Flow<Pair<Double, Double>>
    fun getCategoryTotals(startDate: LocalDate, endDate: LocalDate): Flow<List<Pair<Long, Double>>>
    fun getDailySpending(startDate: LocalDate, endDate: LocalDate): Flow<List<DailySpending>>
}
