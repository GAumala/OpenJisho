package com.gaumala.openjisho.backend.lists

import android.content.Context
import com.gaumala.openjisho.R
import com.gaumala.openjisho.backend.db.DictDatabase
import com.gaumala.openjisho.backend.setup.jmdict.JMdictConverter
import com.gaumala.openjisho.backend.setup.kanjidic.KanjidicConverter
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.common.StudyCard
import com.gaumala.openjisho.frontend.my_lists.ListMetadata
import com.gaumala.openjisho.utils.data.MalformedDataException
import com.gaumala.openjisho.utils.error.UserFriendlyException
import java.io.File
import java.io.IOException

interface ListsDao {

    fun getAllMetadata(): List<ListMetadata>
    fun updateList(name: String, newValue: List<StudyCard>)
    fun getListByName(name: String): List<StudyCard>
    fun deleteListByName(name: String)
    fun createNewList(name: String)

    class Default(ctx: Context): ListsDao {
        private val listsDir = File(ctx.cacheDir, "lists")
        private val printer = StudyListJSONPrinter(createPrinterDB(ctx))

        init {
            listsDir.mkdir()
        }

        override fun getAllMetadata(): List<ListMetadata> {
            return listsDir.listFiles().toList().map {
                ListMetadata(it.nameWithoutExtension)
            }
        }

        private fun getListFileName(name: String) = "$name.${printer.ext}"

        override fun createNewList(name: String) {
            if (name.contains("/"))
                throw UserFriendlyException(
                    R.string.forward_slash_not_allowed)

            val currentLists = getAllMetadata()
            val nameAlreadyExists = currentLists.any { it.name == name }
            if (nameAlreadyExists)
                throw UserFriendlyException(
                    R.string.list_with_that_name_already_exists)

            updateList(name, emptyList())
        }

        override fun updateList(name: String, newValue: List<StudyCard>) {
            val file = File(listsDir, getListFileName(name))
            val text = printer.print(newValue)
            file.writeText(text)
        }

        override fun getListByName(name: String): List<StudyCard> {
            val file = File(listsDir, getListFileName(name))
            val text = file.readText()
            try {
                return printer.scan(text)
            } catch (ex: MalformedDataException) {
                throw IOException("Failed to load list. Is file corrupted?")
            }
        }

        override fun deleteListByName(name: String) {
            val file = File(listsDir, getListFileName(name))
            file.delete()
        }
    }

    companion object {

        private fun createPrinterDB(ctx: Context): StudyListJSONPrinter.DB {
            val dao = DictDatabase.getInstance(ctx).dictQueryDao()
            return object: StudyListJSONPrinter.DB {
                override fun getJMDictEntry(id: Long): JMdictEntry? {
                    val row = dao.lookupJMdictRowById(id) ?: return null
                    return JMdictConverter.fromEntryRow(row)
                }

                override fun getKanjidicEntry(literal: String): KanjidicEntry? {
                    val row = dao.lookupKanjidicRowExact(literal) ?: return null
                    return KanjidicConverter.fromKanjiRow(row)
                }

            }
        }

    }
}
