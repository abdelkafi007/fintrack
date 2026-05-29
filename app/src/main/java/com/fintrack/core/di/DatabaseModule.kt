package com.fintrack.core.di

import android.content.Context
import androidx.room.Room
import com.fintrack.data.local.FinTrackDatabase
import com.fintrack.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinTrackDatabase {
        return Room.databaseBuilder(
            context,
            FinTrackDatabase::class.java,
            FinTrackDatabase.DATABASE_NAME
        )
            .addCallback(FinTrackDatabase.Companion.SeedDatabaseCallback())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTransactionDao(database: FinTrackDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideCategoryDao(database: FinTrackDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideAccountDao(database: FinTrackDatabase): AccountDao = database.accountDao()

    @Provides
    fun provideBudgetDao(database: FinTrackDatabase): BudgetDao = database.budgetDao()

    @Provides
    fun provideGoalDao(database: FinTrackDatabase): GoalDao = database.goalDao()
}
