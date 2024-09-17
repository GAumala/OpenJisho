package com.gaumala.openjisho.frontend.user_sentence

import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.gaumala.mvi.ActionSink
import com.gaumala.mvi.BaseUI
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import com.gaumala.openjisho.frontend.sentence.recycler.SentenceItemFactory
import com.gaumala.openjisho.frontend.user_sentence.actions.SetSentence
import com.gaumala.openjisho.utils.image.MatrixCalculator
import com.gaumala.openjisho.utils.image.MatrixImageView
import com.xwray.groupie.GroupieAdapter

class UserSentenceUI(
    owner: LifecycleOwner,
    showEntry: (JMdictEntry.Summarized) -> Unit,
    private val sink: ActionSink<UserSentenceState, UserSentenceSideEffect>,
    private val onRadicalSearchButtonClicked: (String, List<Int>) -> Unit,
    private val onBackPressedCallback: OnBackPressedCallback,
    isTransitioning: Boolean,
    initialText: String,
    view: View,
    liveState: LiveData<UserSentenceState>,
) : BaseUI<UserSentenceState>(owner, liveState) {

    private val container = view.findViewById<View>(R.id.container)
    private val recycler = view.findViewById<RecyclerView>(R.id.recycler)
    private val sentenceTextView = view.findViewById<TextView>(R.id.sentence_text)
    private val sentenceInput = view.findViewById<EditText>(R.id.sentence_input)
    private val backButton = view.findViewById<View>(R.id.back_button)
    private val radicalSearchButton = view.findViewById<View>(R.id.radical_search_icon)
    private val welcomeGroup: View = view.findViewById(R.id.welcome_group)
    private val adapter = GroupieAdapter()
    private val itemFactory = SentenceItemFactory(showEntry)

    private val sentenceInputWatcher = object : TextWatcher {
        var isEnabled = true
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (!isEnabled)
                return

            val newText = s!!.toString()

            sink.submitAction(
                SetSentence(sentence = newText)
            )
        }
    }

    init {
        setupArt(view)
        setupEditText()
        setupRecycler()

        if (initialText.isNotEmpty()) {
            restoreSavedState(initialText)

            if (isTransitioning) {
                container.visibility = View.GONE
            }
        }
    }

    private fun restoreSavedState(initialText: String) {
        toggleWordsRecycler(true)
        sentenceInput.setText(initialText)
    }

    private fun setupArt(view: View) {
        val welcomeArtView: MatrixImageView = view.findViewById(R.id.welcome_us_art)
        welcomeArtView.matrixCalculator = MatrixCalculator.FitTop()

        val rawHtml = view.context.getString(R.string.dict_welcome_user_sentence)
        val welcomeText = view.findViewById<TextView>(R.id.welcome_us_text)
        welcomeText.text = HtmlCompat.fromHtml(
            rawHtml,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        welcomeText.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun setupEditText() {
        backButton.setOnClickListener { onBackPressedCallback.handleOnBackPressed() }
        sentenceInput.addTextChangedListener(sentenceInputWatcher)
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

    private fun toggleWordsRecycler(visible: Boolean) {
        welcomeGroup.visibility = if (visible) View.INVISIBLE else View.VISIBLE
        recycler.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }


    override fun rebind(state: UserSentenceState) {
        sentenceTextView.text = state.indices.joinToString(separator = "") { it.sentenceForm }
        val items = itemFactory.createItems(null, state.words)
        adapter.update(items)

        toggleWordsRecycler(items.isNotEmpty())

        radicalSearchButton.setOnClickListener {
            onRadicalSearchButtonClicked(state.text, getTransitionBottomTargets())
        }
    }

    fun saveState(): UserSentenceSavedState =
        UserSentenceSavedState(this.liveState.value?.text ?: "")

    private fun getTransitionBottomTargets(): List<Int> {
        return if (welcomeGroup.visibility == View.VISIBLE) {
            listOf(R.id.welcome_us_art, R.id.welcome_us_text)
        } else {
            listOf(R.id.recycler)
        }
    }

    fun onTransitionEnd() {
        toggleWordsRecycler(sentenceInput.text.isNotEmpty())
        container.visibility = View.VISIBLE
    }
}