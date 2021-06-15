package com.gaumala.openjisho.frontend.study_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.R

class ComposeTextFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        val view = inflater.inflate(R.layout.free_text_fragment, null)
        bindView(view)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                requireActivity().onBackPressed()
        }
        return true
    }

    private fun bindView(view: View) {
        val editText = view.findViewById<EditText>(R.id.edit_text)
        val submitBtn = view.findViewById<View>(R.id.submit_btn)

        submitBtn.setOnClickListener {
            val text = editText.text
            if (text.isNotEmpty())
                submitText(text.toString())
        }

        setupActionBar(view)
    }

    private fun setupActionBar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.add_text)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun submitText(text: String) {
        val listener = activity as OnTextSubmittedListener
        listener.onTextSubmitted(text)
    }
}