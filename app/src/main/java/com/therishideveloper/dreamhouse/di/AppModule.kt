package com.therishideveloper.dreamhouse.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.therishideveloper.dreamhouse.data.database.AppDatabase
import com.therishideveloper.dreamhouse.data.dao.NoteDao // Dao import করুন
import com.therishideveloper.dreamhouse.data.dao.ProjectDao
import com.therishideveloper.dreamhouse.data.dao.StageDao
import com.therishideveloper.dreamhouse.data.dao.TransactionDao
import com.therishideveloper.dreamhouse.domain.repository.NoteRepository
import com.therishideveloper.dreamhouse.domain.repository.NoteRepositoryImpl
import com.therishideveloper.dreamhouse.domain.repository.TransactionRepository
import com.therishideveloper.dreamhouse.domain.repository.TransactionRepositoryImpl
import com.therishideveloper.dreamhouse.repository.ProjectRepository
import com.therishideveloper.dreamhouse.repository.ProjectRepositoryImpl
import com.therishideveloper.dreamhouse.util.BackupHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "daily_expense_db"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    @Singleton
    fun provideTransactionRepository(dao: TransactionDao): TransactionRepository {
        return TransactionRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()

    @Provides
    @Singleton
    fun provideNoteRepository(dao: NoteDao): NoteRepository {
        return NoteRepositoryImpl(dao)
    }


    @Provides
    @Singleton
    fun provideProjectDao(db: AppDatabase): ProjectDao = db.projectDao()

    @Provides
    @Singleton
    fun provideStageDao(db: AppDatabase): StageDao = db.stageDao()

    @Provides
    @Singleton
    fun provideProjectRepository(dao: ProjectDao, stageDao: StageDao): ProjectRepository {
        return ProjectRepositoryImpl(dao, stageDao)
    }

    @Provides
    @Singleton
    fun provideBackupHelper(
        @ApplicationContext context: Context
    ): BackupHelper {
        return BackupHelper(context)
    }
}