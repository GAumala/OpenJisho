package com.gaumala.openjisho.frontend.study_list.recycler

import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.StudyCard
import com.gaumala.openjisho.frontend.my_lists.recycler.WelcomeItem
import com.gaumala.openjisho.frontend.study_list.LoadedStudyCards
import com.xwray.groupie.viewbinding.BindableItem


class StudyListItemFactory(private val onItemClicked: (StudyCard) -> Unit) {

    private fun createReadyItems(cards: List<StudyCard>): List<BindableItem<*>> {
        if (cards.isEmpty())
            return listOf(WelcomeItem(R.string.study_list_welcome))

        return cards.map{ item ->
            val onClicked = { onItemClicked(item) }
            when (item) {
                is StudyCard.Text ->
                    SCTextItem(item.id, item.text, onClicked)
                is StudyCard.JMdict ->
                    SCJMdictItem(item.id, item.summarized, onClicked)
                is StudyCard.Kanjidic ->
                    SCKanjidicItem(item.id, item.entry, onClicked)
                is StudyCard.Sentence ->
                    SCSentenceItem(item.id,
                        japanese = item.japanese,
                        english = item.english,
                        onClicked = onClicked)
                is StudyCard.NotFound ->
                    SCTextItem(item.id, "N/A") {}
            } as BindableItem<*>
        }
    }

   fun create(cards: LoadedStudyCards): List<BindableItem<*>>  {
       if (cards is LoadedStudyCards.Loading)
           return listOf(SCLoadingItem())

      if (cards is LoadedStudyCards.Ready)
          return createReadyItems(cards.list)

      return emptyList()
   }
}