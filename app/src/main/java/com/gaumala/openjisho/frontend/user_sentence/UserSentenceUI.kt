package com.gaumala.openjisho.frontend.user_sentence

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

class UserSentenceUI(
    owner: LifecycleOwner,
    showEntry: (JMdictEntry.Summarized) -> Unit,
    private val editSentence: (String) -> Unit,
    view: View,
    liveState: LiveData<UserSentenceState>
) : BaseUI<UserSentenceState>(owner, liveState) {

    private val recycler = view.findViewById<RecyclerView>(R.id.recycler)
    private val sentenceTextView = view.findViewById<TextView>(R.id.sentence_text)
    private val editButton = view.findViewById<View>(R.id.speed_dial_fab)
    private val adapter = GroupieAdapter()
    private val itemFactory = SentenceItemFactory(showEntry)

    init {
        setupEditButton()
        setupRecycler()
    }

    private fun setupEditButton() {
        editButton.visibility = View.VISIBLE
        editButton.setOnClickListener {
            val sentence = this.liveState.value?.text ?: return@setOnClickListener
            editSentence(sentence)
        }
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

    override fun rebind(state: UserSentenceState) {
        sentenceTextView.text = state.indices.joinToString(separator = "") { it.sentenceForm }
        val items = itemFactory.createItems(null, state.words)
        adapter.update(items)
    }

    fun getSavedText(): String? = this.liveState.value?.text
}