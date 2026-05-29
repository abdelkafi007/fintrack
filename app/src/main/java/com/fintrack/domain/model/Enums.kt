package com.fintrack.domain.model

/** Type of financial transaction */
enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

/** Recurrence period for transactions */
enum class TransactionPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

/** Budget tracking period */
enum class BudgetPeriod {
    WEEKLY,
    MONTHLY
}

/** Type of financial account */
enum class AccountType {
    CASH,
    BANK,
    CREDIT_CARD,
    SAVINGS
}

/** Category classification */
enum class CategoryType {
    INCOME,
    EXPENSE
}
