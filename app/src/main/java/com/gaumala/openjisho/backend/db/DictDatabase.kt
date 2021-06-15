package com.gaumala.openjisho.backend.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [
    JMdictRow::class,
    KanjidicRow::class,
    EngKeywordRow::class,
    JpnKeywordRow::class,
    TagRow::class,
    RadicalRow::class,
    JpnSentenceRow::class,
    JpnIndicesRow::class,
    EngTranslationRow::class],
    version = 1, exportSchema = false)
abstract class DictDatabase: RoomDatabase() {
    abstract fun setupDao(): SetupDao
    abstract fun dictQueryDao(): DictQueryDao

    companion object {
        const val DATABASE_NAME = "dict_db"
        // For Singleton instantiation
        @Volatile private var instance: DictDatabase? = null

        fun getInstance(context: Context): DictDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): DictDatabase {
            return Room.databaseBuilder(context,
                DictDatabase::class.java, DATABASE_NAME)
                    .build()
        }
    }
}