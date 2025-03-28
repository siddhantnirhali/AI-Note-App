package com.example.noteapp.core.di

import com.example.noteapp.damain.usecase.ScheduleReminderUseCase
import android.content.Context
import com.example.noteapp.data.local.NoteDao
import com.example.noteapp.data.local.NoteDatabase
import com.example.noteapp.data.repository.NoteRepository
import com.example.noteapp.data.repository.NoteRepositoryImpl
import com.example.noteapp.core.navigation.NavigationProvider
import com.example.noteapp.data.repository.GeminiRepository
import com.example.noteapp.data.repository.GeminiRepositoryImpl
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
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase {
        return NoteDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideNoteDao(noteDatabase: NoteDatabase): NoteDao = noteDatabase.noteDao()

    @Provides
    @Singleton
    fun provideNoteRepository(
        noteDao: NoteDao, // Assuming you have NoteDao injected in the module
    ): NoteRepository {
        return NoteRepositoryImpl(noteDao)
    }

    @Provides
    @Singleton
    fun provideGeminiRepository(): GeminiRepository {
        return GeminiRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideNavigationProvider(): NavigationProvider {
        return NavigationProvider()
    }

    @Provides
    @Singleton
    fun provideScheduleReminderUseCase(@ApplicationContext context: Context): ScheduleReminderUseCase {
        return ScheduleReminderUseCase(context)
    }
}
