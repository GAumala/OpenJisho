package com.gaumala.openjisho.frontend.my_lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.navigation.MainScreen
import com.gaumala.openjisho.frontend.navigation.Navigator
import com.gaumala.openjisho.frontend.dict.DictSavedState
import com.gaumala.openjisho.frontend.navigation.NavDrawerContainer
import com.gaumala.openjisho.frontend.navigation.runSlideTransition
import com.gaumala.openjisho.frontend.study_list.*
import com.gaumala.openjisho.utils.ui.AbstractSnackbar
import com.gaumala.openjisho.utils.ui.ContextualToolbarMenu

class MyListsFragment : Fragment() {

    private lateinit var ui: MyListsUI
    private val viewModel by lazy {
        ViewModelProvider(this, MyListsViewModel.Factory(this))
            .get(MyListsViewModel::class.java)
    }
    private val toolbarMenu: ContextualToolbarMenu by lazy {
        ContextualToolbarMenu.Default(
            requireActivity() as AppCompatActivity,
            R.menu.study_list_contextual_menu)
    }

    private val navigator = object: MyListsNavigator {
        override fun goToList(listName: String) {
            val savedState = requireArguments()
                .getParcelable<DictSavedState>(DICT_SAVED_STATE_KEY)

            parentFragmentManager.runSlideTransition(
                StudyListFragment.newInstance(
                    savedState,
                    listName
                )
            )
        }

        override fun goToNewListForm() {
            val savedState = requireArguments()
                .getParcelable<DictSavedState>(DICT_SAVED_STATE_KEY)
            val nextFragment =
                NewListFragment.newInstance(
                    savedState
                )
            parentFragmentManager.runSlideTransition(nextFragment)
        }

    }

    private val onBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val savedState = requireArguments()
                .getParcelable<DictSavedState>(DICT_SAVED_STATE_KEY)
            val screen = MainScreen.Dictionary(
                reverse = true, savedState = savedState)

            val navigator = requireActivity() as Navigator
            navigator.goTo(screen)
        }
    }

    private val drawerContainer by lazy {
        requireActivity() as NavDrawerContainer
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        val act = requireActivity()
        act.onBackPressedDispatcher
            .addCallback(this, onBackPressedCallback)

        val view = inflater.inflate(
            R.layout.my_lists_fragment, container, false)
        val snackBar = createSnackbar(view)

        ui = MyListsUI(owner = this.viewLifecycleOwner,
            view = view,
            navigator = navigator,
            toolbarMenu = toolbarMenu,
            snackBar = snackBar,
            sink = viewModel.userActionSink,
            liveState = viewModel.liveState)
        ui.subscribe()

        setupActivityToolbar(view)

        return view
    }

    private fun createSnackbar(view: View): AbstractSnackbar {
        val container = view.findViewById<ViewGroup>(R.id.container)
        return AbstractSnackbar.Default(container)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val drawerContainer = requireActivity() as NavDrawerContainer
                drawerContainer.openDrawer()
            }
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        drawerContainer.setDrawerLocked(false)
    }

    override fun onStop() {
        super.onStop()
        drawerContainer.setDrawerLocked(true)
    }

    private fun setupActivityToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        val act = activity as AppCompatActivity
        act.setSupportActionBar(toolbar)
    }

    companion object {
        private const val DICT_SAVED_STATE_KEY = "dictSavedState"

        fun newInstance(savedState: DictSavedState?): MyListsFragment {
            val args = Bundle()
            args.putParcelable(DICT_SAVED_STATE_KEY, savedState)

            val f = MyListsFragment()
            f.arguments = args
            return f
        }
    }
}
