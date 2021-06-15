package com.gaumala.openjisho.frontend.entry

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.JMdictEntry
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.gaumala.openjisho.common.KanjidicEntry
import com.gaumala.openjisho.utils.ClipboardUtil

/**
 * A fragment that displays a dictionary entry's details. It supports JMdict
 * entries and KanjidicEntries.
 *
 * The user usually navigates here after clicking a result row in
 * [com.gaumala.openjisho.frontend.dict.DictFragment].
 */
class EntryFragment: Fragment() {
    companion object {
        const val JMDICT_ENTRY_KEY = "jmDictEntry"
        const val JMDICT_TITLE_KEY = "jmdictTitleKey"
        const val KANJIDIC_ENTRY_KEY = "kanjidicEntry"

        fun newInstance(entry: JMdictEntry, title: String): EntryFragment {
            val bundle = Bundle()
            bundle.putParcelable(JMDICT_ENTRY_KEY, entry)
            bundle.putString(JMDICT_TITLE_KEY, title)

            val fragment = EntryFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(entry: KanjidicEntry): EntryFragment {
            val bundle = Bundle()
            bundle.putParcelable(KANJIDIC_ENTRY_KEY, entry)

            val fragment = EntryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            R.id.copy -> {
                copyJMdictHeadwordToClipboard()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun copyJMdictHeadwordToClipboard() {
        ClipboardUtil.copy(requireContext(), getTitle())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (getTitle().isNotEmpty()) // has jmdict entry
            inflater.inflate(R.menu.entry_detail_menu, menu)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val viewModel = ViewModelProviders.
            of(this, EntryViewModel.Factory(this))
            .get(EntryViewModel::class.java)

        val view = inflater.inflate(R.layout.entry_fragment, container, false)
        setupBannerArt(view)
        setupToolbar(activity as AppCompatActivity, view)
        EntryUI(owner = this.viewLifecycleOwner,
            view = view,
            liveState = viewModel.liveState).subscribe()

        return view
    }

    private fun getTitle(): String {
        return requireArguments().getString(JMDICT_TITLE_KEY, "")
    }

    private fun setupBannerArt(view: View) {
        val banner = view.findViewById<ImageView>(R.id.app_bar_art)
        val isKanjiOnly = requireArguments()
            .getParcelable<KanjidicEntry>(KANJIDIC_ENTRY_KEY) != null
        if (isKanjiOnly) {
            banner.scaleType = ImageView.ScaleType.FIT_CENTER
            banner.setImageResource(R.drawable.sentence_banner_art)
        }  else {
            banner.scaleType = ImageView.ScaleType.FIT_END
            banner.setImageResource(R.drawable.entry_banner_art)
            val endPadding = view.context.resources.getDimension(
                R.dimen.entry_banner_art_end_padding).toInt()
            banner.setPadding(0, 0, endPadding, 0)

        }
    }


    private fun setupToolbar(activity: AppCompatActivity, view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getTitle()
        activity.setSupportActionBar(toolbar)

        val actionBar = activity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
}