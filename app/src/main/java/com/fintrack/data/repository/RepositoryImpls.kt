package com.fintrack.data.repository

import com.fintrack.data.local.dao.*
import com.fintrack.data.mapper.*
import com.fintrack.domain.model.*
import com.fintrack.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    override fun getTransactionById(id: Long): Flow<Transaction?> =
        transactionDao.getTransactionById(id).map { it?.toDomain() }

    override fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate).map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCategory(categoryId).map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByAccount(accountId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByAccount(accountId).map { list -> list.map { it.toDomain() } }

    override fun searchTransactions(query: String): Flow<List<Transaction>> =
        transactionDao.searchTransactions(query).map { list -> list.map { it.toDomain() } }

    override suspend fun addTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction.toEntity())

    override suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction.toEntity())
}

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories().map { list -> list.map { it.toDomain() } }

    override fun getCategoriesByType(type: CategoryType): Flow<List<Category>> =
        categoryDao.getCategoriesByType(type).map { list -> list.map { it.toDomain() } }

    override fun getCategoryById(id: Long): Flow<Category?> =
        categoryDao.getCategoryById(id).map { it?.toDomain() }

    override suspend fun addCategory(category: Category): Long =
        categoryDao.insertCategory(category.toEntity())

    override suspend fun updateCategory(category: Category) =
        categoryDao.updateCategory(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        categoryDao.deleteCategory(category.toEntity())
}

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override fun getAllAccounts(): Flow<List<Account>> =
        accountDao.getAllAccounts().map { list -> list.map { it.toDomain() } }

    override fun getActiveAccounts(): Flow<List<Account>> =
        accountDao.getActiveAccounts().map { list -> list.map { it.toDomain() } }

    override fun getAccountById(id: Long): Flow<Account?> =
        accountDao.getAccountById(id).map { it?.toDomain() }

    override suspend fun getAccountByIdSync(id: Long): Account? =
        accountDao.getAccountByIdSync(id)?.toDomain()

    override fun getTotalBalance(): Flow<Double> =
        accountDao.getTotalBalance()

    override suspend fun addAccount(account: Account): Long =
        accountDao.insertAccount(account.toEntity())

    override suspend fun updateAccount(account: Account) =
        accountDao.updateAccount(account.toEntity())

    override suspend fun deleteAccount(account: Account) =
        accountDao.deleteAccount(account.toEntity())
}

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao
) : BudgetRepository {

    override fun getAllBudgets(): Flow<List<Budget>> =
        budgetDao.getAllBudgets().map { list ->
            list.map { entity ->
                val category = categoryDao.getCategoryByIdSync(entity.categoryId)
                entity.toDomain(
                    categoryName = category?.name ?: "Unknown",
                    categoryIcon = category?.icon ?: "category",
                    categoryColor = category?.color ?: 0xFF94A3B8
                )
            }
        }

    override fun getBudgetById(id: Long): Flow<Budget?> =
        budgetDao.getBudgetById(id).map { entity ->
            entity?.let {
                val category = categoryDao.getCategoryByIdSync(it.categoryId)
                it.toDomain(
                    categoryName = category?.name ?: "Unknown",
                    categoryIcon = category?.icon ?: "category",
                    categoryColor = category?.color ?: 0xFF94A3B8
                )
            }
        }

    override fun getBudgetByCategory(categoryId: Long): Flow<Budget?> =
        budgetDao.getBudgetByCategory(categoryId).map { entity ->
            entity?.let {
                val category = categoryDao.getCategoryByIdSync(it.categoryId)
                it.toDomain(
                    categoryName = category?.name ?: "Unknown",
                    categoryIcon = category?.icon ?: "category",
                    categoryColor = category?.color ?: 0xFF94A3B8
                )
            }
        }

    override suspend fun addBudget(budget: Budget): Long =
        budgetDao.insertBudget(budget.toEntity())

    override suspend fun updateBudget(budget: Budget) =
        budgetDao.updateBudget(budget.toEntity())

    override suspend fun deleteBudget(budget: Budget) =
        budgetDao.deleteBudget(budget.toEntity())
}

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override fun getAllGoals(): Flow<List<Goal>> =
        goalDao.getAllGoals().map { list -> list.map { it.toDomain() } }

    override fun getActiveGoals(): Flow<List<Goal>> =
        goalDao.getActiveGoals().map { list -> list.map { it.toDomain() } }

    override fun getCompletedGoals(): Flow<List<Goal>> =
        goalDao.getCompletedGoals().map { list -> list.map { it.toDomain() } }

    override fun getGoalById(id: Long): Flow<Goal?> =
        goalDao.getGoalById(id).map { it?.toDomain() }

    override suspend fun addGoal(goal: Goal): Long =
        goalDao.insertGoal(goal.toEntity())

    override suspend fun updateGoal(goal: Goal) =
        goalDao.updateGoal(goal.toEntity())

    override suspend fun deleteGoal(goal: Goal) =
        goalDao.deleteGoal(goal.toEntity())
}

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : AnalyticsRepository {

    override fun getMonthlyTotals(startDate: LocalDate, endDate: LocalDate): Flow<Pair<Double, Double>> =
        transactionDao.getMonthlyTotals(startDate, endDate).map { Pair(it.income, it.expense) }

    override fun getCategoryTotals(startDate: LocalDate, endDate: LocalDate): Flow<List<Pair<Long, Double>>> =
        transactionDao.getCategoryTotals(startDate, endDate).map { list ->
            list.map { Pair(it.categoryId, it.total) }
        }

    override fun getDailySpending(startDate: LocalDate, endDate: LocalDate): Flow<List<DailySpending>> =
        transactionDao.getDailySpending(startDate, endDate).map { list ->
            list.map { DailySpending(it.date, it.total) }
        }
}
