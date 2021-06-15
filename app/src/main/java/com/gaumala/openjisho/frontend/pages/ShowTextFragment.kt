package com.gaumala.openjisho.frontend.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.databinding.ShowTextFragmentBinding

class ShowTextFragment: Fragment() {

    private val headerText by lazy {
        requireArguments().getString(HEADER_TEXT_KEY)!!
    }
    private val subText by lazy {
        requireArguments().getString(SUB_TEXT_KEY) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        val binding = ShowTextFragmentBinding.inflate(
            inflater, container, false)
        bindView(binding)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun bindView(binding: ShowTextFragmentBinding) {
        binding.headerTextView.text = headerText
        binding.subTextView.text = subText

        setupToolbar(binding.toolbar)
    }
    private fun setupToolbar(toolbar: Toolbar) {
        toolbar.title = ""
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)

        val actionBar = activity.supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        fun newInstance(headerText: String, subText: String): AboutFragment {
            val args = Bundle()
            args.putString(HEADER_TEXT_KEY, headerText)
            args.putString(SUB_TEXT_KEY, subText)
            return AboutFragment()
        }

        const val HEADER_TEXT_KEY = "headerText"
        const val SUB_TEXT_KEY = "subText"
    }
}