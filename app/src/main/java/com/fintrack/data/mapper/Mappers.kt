package com.fintrack.data.mapper

import com.fintrack.data.local.entity.*
import com.fintrack.domain.model.*

// ====== Transaction Mappers ======

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    amount = amount,
    type = type,
    categoryId = categoryId,
    accountId = accountId,
    toAccountId = toAccountId,
    date = date,
    notes = notes,
    receiptUri = receiptUri,
    isRecurring = isRecurring,
    recurringPeriod = recurringPeriod,
    currencyCode = currencyCode,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    amount = amount,
    type = type,
    categoryId = categoryId,
    accountId = accountId,
    toAccountId = toAccountId,
    date = date,
    notes = notes,
    receiptUri = receiptUri,
    isRecurring = isRecurring,
    recurringPeriod = recurringPeriod,
    currencyCode = currencyCode,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ====== Category Mappers ======

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    icon = icon,
    color = color,
    parentId = parentId,
    isDefault = isDefault,
    type = type
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    icon = icon,
    color = color,
    parentId = parentId,
    isDefault = isDefault,
    type = type
)

// ====== Account Mappers ======

fun AccountEntity.toDomain() = Account(
    id = id,
    name = name,
    type = type,
    balance = balance,
    currencyCode = currencyCode,
    icon = icon,
    color = color,
    isArchived = isArchived
)

fun Account.toEntity() = AccountEntity(
    id = id,
    name = name,
    type = type,
    balance = balance,
    currencyCode = currencyCode,
    icon = icon,
    color = color,
    isArchived = isArchived
)

// ====== Budget Mappers ======

fun BudgetEntity.toDomain(
    categoryName: String = "",
    categoryIcon: String = "",
    categoryColor: Long = 0
) = Budget(
    id = id,
    categoryId = categoryId,
    categoryName = categoryName,
    categoryIcon = categoryIcon,
    categoryColor = categoryColor,
    amount = amount,
    period = period,
    startDate = startDate,
    rollover = rollover,
    spent = 0.0 // Calculated separately from transactions
)

fun Budget.toEntity() = BudgetEntity(
    id = id,
    categoryId = categoryId,
    amount = amount,
    period = period,
    startDate = startDate,
    rollover = rollover
)

// ====== Goal Mappers ======

fun GoalEntity.toDomain() = Goal(
    id = id,
    name = name,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    deadline = deadline,
    icon = icon,
    color = color,
    isCompleted = isCompleted
)

fun Goal.toEntity() = GoalEntity(
    id = id,
    name = name,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    deadline = deadline,
    icon = icon,
    color = color,
    isCompleted = isCompleted
)
