package com.gaumala.openjisho.frontend.tour

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.navigation.MainScreen
import com.gaumala.openjisho.frontend.navigation.Navigator
import com.gaumala.openjisho.utils.SystemUIHelper
import com.gaumala.openjisho.utils.indicator.PageIndicator

/**
 * A fragment that displays a view pager with a "tour" of the app's
 * main features.
 */
class TourFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val view = inflater.inflate(
            R.layout.tour_fragment, container, false)
        bindView(view)

        SystemUIHelper(this).matchWithSurface()
        return view
    }

    private fun close(isRunningSetup: Boolean) {
        if (isRunningSetup) {
            val navigator = activity as Navigator
            navigator.goTo(MainScreen.Setup)
        } else
            requireActivity().onBackPressed()
    }

    private fun setupPager(pager: ViewPager, indicator: PageIndicator) {
        pager.adapter = TourPageAdapter(childFragmentManager)
        pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {
                indicator.pageIndex = position
            }
        })
    }

    private fun bindView(view: View) {
        val isRunningSetup = requireArguments()
            .getBoolean(IS_RUNNING_SETUP_KEY)
        val closeListener = View.OnClickListener { close(isRunningSetup) }

        val pager = view.findViewById<ViewPager>(R.id.pager)
        val indicator = view.findViewById<PageIndicator>(R.id.page_indicator)
        setupPager(pager, indicator)

        val closeButton = view.findViewById<View>(R.id.close_btn)
        closeButton.setOnClickListener(closeListener)

        val skipButton = view.findViewById<View>(R.id.skip_btn)
        if (isRunningSetup)
            skipButton.setOnClickListener(closeListener)
        else
            skipButton.visibility = View.INVISIBLE
    }

    companion object {
        const val IS_RUNNING_SETUP_KEY = "isRunningSetup"

        fun newInstance(isRunningSetup: Boolean): TourFragment {
            val args = Bundle()
            args.putBoolean(IS_RUNNING_SETUP_KEY, isRunningSetup)

            val f = TourFragment()
            f.arguments = args
            return f
        }
    }

}