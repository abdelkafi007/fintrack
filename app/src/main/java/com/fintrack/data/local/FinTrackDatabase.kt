package com.fintrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fintrack.data.local.converter.Converters
import com.fintrack.data.local.dao.*
import com.fintrack.data.local.entity.*
import com.fintrack.domain.model.CategoryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        BudgetEntity::class,
        GoalEntity::class,
        TagEntity::class,
        TransactionTagCrossRef::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FinTrackDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun goalDao(): GoalDao

    companion object {
        const val DATABASE_NAME = "fintrack.db"

        /**
         * Callback to seed default categories on first database creation.
         */
        class SeedDatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    seedDefaultCategories(db)
                }
            }
        }
    }
}

/**
 * Seeds default income and expense categories into the database.
 */
private fun seedDefaultCategories(db: SupportSQLiteDatabase) {
    val incomeCategories = listOf(
        Triple("Salary", "work", 0xFF10B981),
        Triple("Freelance", "laptop", 0xFF06B6D4),
        Triple("Investments", "trending_up", 0xFF8B5CF6),
        Triple("Gifts", "card_giftcard", 0xFFF472B6),
        Triple("Other Income", "attach_money", 0xFF6EE7B7)
    )

    val expenseCategories = listOf(
        Triple("Food & Dining", "restaurant", 0xFFF43F5E),
        Triple("Transportation", "directions_car", 0xFF3B82F6),
        Triple("Housing", "home", 0xFF8B5CF6),
        Triple("Utilities", "bolt", 0xFFF59E0B),
        Triple("Entertainment", "movie", 0xFFEC4899),
        Triple("Shopping", "shopping_bag", 0xFF14B8A6),
        Triple("Healthcare", "local_hospital", 0xFFEF4444),
        Triple("Education", "school", 0xFF6366F1),
        Triple("Personal Care", "spa", 0xFFD946EF),
        Triple("Travel", "flight", 0xFF0EA5E9),
        Triple("Subscriptions", "subscriptions", 0xFF64748B),
        Triple("Other", "more_horiz", 0xFF94A3B8)
    )

    incomeCategories.forEach { (name, icon, color) ->
        db.execSQL(
            "INSERT INTO categories (name, icon, color, parentId, isDefault, type) VALUES (?, ?, ?, NULL, 1, 'INCOME')",
            arrayOf(name, icon, color)
        )
    }

    expenseCategories.forEach { (name, icon, color) ->
        db.execSQL(
            "INSERT INTO categories (name, icon, color, parentId, isDefault, type) VALUES (?, ?, ?, NULL, 1, 'EXPENSE')",
            arrayOf(name, icon, color)
        )
    }
}
