package com.example.noteapp.data.local

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.noteapp.core.utils.Converters
import com.example.noteapp.damain.model.Note

@Database(entities = [Note::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class) // Register TypeConverters
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "notes_db"
                )
                    .addMigrations(MIGRATION_5_6) // Add the migration here
                    .build()
                INSTANCE = instance
                instance
            }
        }
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new column `isPinned` with a default value of 0 (false)
                db.execSQL("ALTER TABLE notes ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new column `isPinned` with a default value of 0 (false)
                db.execSQL("ALTER TABLE notes ADD COLUMN isSelected INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add the new column with a default value (White in ARGB format)
                db.execSQL("ALTER TABLE notes ADD COLUMN noteBackgroundColor INTEGER NOT NULL DEFAULT ${Color.White.toArgb()}")
            }
        }

        private val MIGRATION_4_3 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add new column for DateTimeReminder as JSON string
                db.execSQL("ALTER TABLE notes ADD COLUMN dateTimeReminder TEXT DEFAULT NULL")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add the new "label" column with default empty JSON list
                db.execSQL("ALTER TABLE notes ADD COLUMN label TEXT NOT NULL DEFAULT '[]'")
            }
        }
    }
}
