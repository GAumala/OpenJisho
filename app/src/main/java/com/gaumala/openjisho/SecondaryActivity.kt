package com.gaumala.openjisho

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gaumala.openjisho.frontend.navigation.SecondaryScreen
import com.gaumala.openjisho.frontend.dict.DictFragment
import com.gaumala.openjisho.frontend.entry.EntryFragment
import com.gaumala.openjisho.frontend.pages.AboutFragment
import com.gaumala.openjisho.frontend.pages.ShowTextFragment
import com.gaumala.openjisho.frontend.sentence.SentenceFragment
import com.gaumala.openjisho.frontend.study_list.ComposeTextFragment
import com.gaumala.openjisho.frontend.study_list.OnTextSubmittedListener
import com.gaumala.openjisho.frontend.tour.TourFragment

/**
 * This activity should display every fragment that makes the
 * user navigate away from MainActivity for a specific goal and
 * then come back. This is mostly used for detail screens.
 */
class SecondaryActivity: AppCompatActivity(), OnTextSubmittedListener {

    private val fragmentAlreadySet
        get() = supportFragmentManager.findFragmentById(R.id.container) != null

    override fun onTextSubmitted(text: String) {
        val intent = Intent()
        intent.putExtra(RESULT_TEXT_KEY, text)
        submitOKResult(intent)
    }

    fun submitOKResult(intent: Intent) {
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.secondary_activity)

        if (fragmentAlreadySet)
            return

        val screen = SecondaryScreen.fromScreenKey(
            intent.getStringExtra(SCREEN_KEY))
        val extraArgs = intent.getBundleExtra(EXTRA_ARGS_KEY)
        addFragment(screen, extraArgs)
    }

    private fun createFragmentFromType(screen: SecondaryScreen) =
        when (screen) {
            SecondaryScreen.showEntry -> EntryFragment()
            SecondaryScreen.composeText -> ComposeTextFragment()
            SecondaryScreen.pickDictEntry -> DictFragment()
            SecondaryScreen.showAppInfo -> AboutFragment()
            SecondaryScreen.showText -> ShowTextFragment()
            SecondaryScreen.showSentence -> SentenceFragment()
            SecondaryScreen.showHelp -> TourFragment()
        }

    private fun addFragment(screen: SecondaryScreen, extraArgs: Bundle) {
        val fragment = createFragmentFromType(screen)
        fragment.arguments = extraArgs

        supportFragmentManager.beginTransaction()
        .replace(R.id.container, fragment)
        .commit()
    }

    companion object {
        const val EXTRA_ARGS_KEY = "extraArgs"
        const val SCREEN_KEY = "screen"

        const val RESULT_TEXT_KEY = "resultText"
        const val RESULT_JMDICT_SUMMARIZED_KEY = "resultJMdictSummarized"
        const val RESULT_KANJIDIC_ENTRY_KEY = "resultKanjidicEntry"
        const val RESULT_SENTENCE_KEY = "resultSentenceEntry"
    }
}