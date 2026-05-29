package com.fintrack.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain model representing a financial transaction.
 */
data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long,
    val accountId: Long,
    val toAccountId: Long? = null,
    val date: LocalDate,
    val notes: String = "",
    val receiptUri: String? = null,
    val isRecurring: Boolean = false,
    val recurringPeriod: TransactionPeriod? = null,
    val currencyCode: String = "USD",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Domain model representing a transaction category.
 */
data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val parentId: Long? = null,
    val isDefault: Boolean = false,
    val type: CategoryType
)

/**
 * Domain model representing a financial account.
 */
data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val balance: Double = 0.0,
    val currencyCode: String = "USD",
    val icon: String,
    val color: Long,
    val isArchived: Boolean = false
)

/**
 * Domain model representing a budget for a category.
 */
data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val categoryName: String = "",
    val categoryIcon: String = "",
    val categoryColor: Long = 0,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: LocalDate,
    val rollover: Boolean = false,
    val spent: Double = 0.0
) {
    val remaining: Double get() = amount - spent
    val progress: Float get() = if (amount > 0) (spent / amount).toFloat().coerceIn(0f, 2f) else 0f
    val isOverBudget: Boolean get() = spent > amount
}

/**
 * Domain model representing a savings goal.
 */
data class Goal(
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: LocalDate? = null,
    val icon: String,
    val color: Long,
    val isCompleted: Boolean = false
) {
    val progress: Float get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    val remaining: Double get() = (targetAmount - currentAmount).coerceAtLeast(0.0)
}

/**
 * Analytics data for monthly overview.
 */
data class MonthlyAnalytics(
    val month: LocalDate,
    val totalIncome: Double,
    val totalExpense: Double,
    val categoryBreakdown: List<CategorySpending>
) {
    val netFlow: Double get() = totalIncome - totalExpense
}

data class CategorySpending(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long,
    val amount: Double,
    val percentage: Float
)

data class DailySpending(
    val date: LocalDate,
    val amount: Double
)
