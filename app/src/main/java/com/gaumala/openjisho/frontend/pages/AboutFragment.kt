package com.gaumala.openjisho.frontend.pages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.BuildConfig
import com.gaumala.openjisho.R
import com.gaumala.openjisho.utils.SystemUIHelper


/**
 * The fragment displayed when user clicks "About" in the drawer menu.
 * It just displays basic information about this app.
 */
class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        val view = inflater.inflate(
            R.layout.about_fragment, container, false
        )
        bindView(view)

        SystemUIHelper(this).matchWithSurface()

        return view
    }

    private fun bindView(view: View) {
        val bodyText = view.findViewById<TextView>(R.id.body_text)
        val versionText = view.findViewById<TextView>(R.id.version_text)
        val ghButton = view.findViewById<TextView>(R.id.github_button)
        val rawBodyHtml = getString(R.string.about_app_text_body)
        versionText.text = BuildConfig.VERSION_NAME
        bodyText.text = HtmlCompat.fromHtml(
            rawBodyHtml,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        bodyText.movementMethod = LinkMovementMethod.getInstance()

        ghButton.setOnClickListener { openGithubLink() }
    }

    private fun openGithubLink() {
        val url = "https://github.com/GAumala/OpenJisho"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}