package com.fintrack.data.local.dao

import androidx.room.*
import com.fintrack.data.local.entity.TransactionEntity
import com.fintrack.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Long): Flow<TransactionEntity?>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getTransactionsByAccount(accountId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE notes LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as income,
               COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as expense
        FROM transactions 
        WHERE date BETWEEN :startDate AND :endDate
    """)
    fun getMonthlyTotals(startDate: LocalDate, endDate: LocalDate): Flow<MonthlyTotal>

    @Query("""
        SELECT categoryId, SUM(amount) as total 
        FROM transactions 
        WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate
        GROUP BY categoryId 
        ORDER BY total DESC
    """)
    fun getCategoryTotals(startDate: LocalDate, endDate: LocalDate): Flow<List<CategoryTotal>>

    @Query("""
        SELECT date, SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as total
        FROM transactions
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date ASC
    """)
    fun getDailySpending(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyTotal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}

/** Helper data class for monthly income/expense totals */
data class MonthlyTotal(
    val income: Double,
    val expense: Double
)

/** Helper data class for category spending totals */
data class CategoryTotal(
    val categoryId: Long,
    val total: Double
)

/** Helper data class for daily spending */
data class DailyTotal(
    val date: LocalDate,
    val total: Double
)
