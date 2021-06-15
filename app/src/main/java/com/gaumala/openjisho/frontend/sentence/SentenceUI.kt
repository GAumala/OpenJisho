package com.gaumala.openjisho.frontend.sentence

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.gaumala.mvi.BaseUI
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.frontend.sentence.recycler.SentenceItemFactory
import com.xwray.groupie.GroupieAdapter

class SentenceUI(owner: LifecycleOwner,
                 showEntry: (JMdictEntry.Summarized) -> Unit,
                 view: View,
                 liveState: LiveData<SentenceState>
): BaseUI<SentenceState>(owner, liveState) {

    private val recycler = view.findViewById<RecyclerView>(R.id.recycler)
    private val sentenceTextView = view.findViewById<TextView>(R.id.sentence_text)
    private val adapter = GroupieAdapter()
    private val itemFactory = SentenceItemFactory(showEntry)

    init {
        setupRecycler()
    }

    private fun setupRecycler() {
        recycler.adapter = adapter

        val ctx = recycler.context
        val dividerItemDecoration = DividerItemDecoration(
            ctx, RecyclerView.VERTICAL
        )
        val dividerDrawable =
            ContextCompat.getDrawable(ctx, R.drawable.list_divider)!!
        dividerItemDecoration.setDrawable(dividerDrawable)
        recycler.addItemDecoration(dividerItemDecoration)
    }

    override fun rebind(state: SentenceState) {
        sentenceTextView.text = state.sentence.japanese
        val items = itemFactory.createItems(state.sentence, state.words)
        adapter.update(items)
    }
}