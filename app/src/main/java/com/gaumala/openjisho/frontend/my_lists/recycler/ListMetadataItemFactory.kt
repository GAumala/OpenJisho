package com.gaumala.openjisho.frontend.my_lists.recycler

import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.my_lists.ListMetadata
import com.gaumala.openjisho.frontend.my_lists.LoadedLists
import com.gaumala.openjisho.frontend.my_lists.SelectableLM
import com.xwray.groupie.viewbinding.BindableItem

class ListMetadataItemFactory(private val onItemLongClicked: (String) -> Unit,
                              private val onItemClicked: (ListMetadata) -> Unit,
                              private val onSelectableItemClicked: (String) -> Unit) {

    private fun createReadyItems(cards: List<ListMetadata>): List<BindableItem<*>> {
        if (cards.isEmpty())
            return listOf(WelcomeItem(R.string.my_lists_welcome))

        return cards.map{ item ->
            val onLongClicked = { onItemLongClicked(item.name) }
            val onClicked = { onItemClicked(item) }
            ListMetadataItem(
                metadata = item,
                onClicked = onClicked,
                onLongClicked = onLongClicked
            )
        }
    }

    private fun createSelectableItems(selectableCards: List<SelectableLM>)
            : List<BindableItem<*>> {
        return selectableCards.map { item ->
            val metadata = item.metadata
            val onClicked = {
                onSelectableItemClicked(metadata.name)
            }

            ListMetadataItem(
                metadata = metadata,
                isSelected = item.isSelected,
                onClicked = onClicked
            )
        }
    }

    fun create(lists: LoadedLists): List<BindableItem<*>>  {
        if (lists is LoadedLists.Ready)
            return createReadyItems(lists.list)

        if (lists is LoadedLists.MultiSelect)
            return createSelectableItems(lists.list)

        return emptyList()
    }
}