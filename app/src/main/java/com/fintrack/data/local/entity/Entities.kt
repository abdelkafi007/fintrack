package com.fintrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fintrack.domain.model.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "transactions",
    indices = [
        Index("categoryId"),
        Index("accountId"),
        Index("date"),
        Index("type")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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

@Entity(tableName = "categories",
    indices = [Index("parentId"), Index("type")]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val parentId: Long? = null,
    val isDefault: Boolean = false,
    val type: CategoryType
)

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: AccountType,
    val balance: Double = 0.0,
    val currencyCode: String = "USD",
    val icon: String,
    val color: Long,
    val isArchived: Boolean = false
)

@Entity(tableName = "budgets",
    indices = [Index("categoryId")]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    val period: BudgetPeriod,
    val startDate: LocalDate,
    val rollover: Boolean = false
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: LocalDate? = null,
    val icon: String,
    val color: Long,
    val isCompleted: Boolean = false
)

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Long
)

@Entity(
    tableName = "transaction_tag_cross_ref",
    primaryKeys = ["transactionId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tagId")]
)
data class TransactionTagCrossRef(
    val transactionId: Long,
    val tagId: Long
)
