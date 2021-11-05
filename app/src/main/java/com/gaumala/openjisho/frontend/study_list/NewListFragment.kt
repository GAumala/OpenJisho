package com.gaumala.openjisho.frontend.study_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.gaumala.openjisho.R
import com.gaumala.openjisho.backend.lists.ListsDao
import com.gaumala.openjisho.frontend.navigation.MainScreen
import com.gaumala.openjisho.frontend.navigation.Navigator
import com.gaumala.openjisho.frontend.dict.DictSavedState
import com.gaumala.openjisho.frontend.navigation.runSlideTransition
import com.gaumala.openjisho.utils.async.CoroutineIOWorker
import com.gaumala.openjisho.utils.error.Either
import com.gaumala.openjisho.utils.error.UserFriendlyException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class NewListFragment: Fragment() {

    private val repository by lazy {
        val ctx = requireContext()
        val scope = viewLifecycleOwner.lifecycleScope
        ListsRepository(
            ListsDao.Default(ctx),
            CoroutineIOWorker(scope)
        )
    }

    private val onBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val savedState = requireArguments()
                .getParcelable<DictSavedState>(DICT_SAVED_STATE_KEY)
            val screen = MainScreen.MyLists(savedState, reverse = true)

            val navigator = requireActivity() as Navigator
            navigator.goTo(screen)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val act = requireActivity() as AppCompatActivity
        act.onBackPressedDispatcher
            .addCallback(this, onBackPressedCallback)

        val view = inflater.inflate(R.layout.new_list_fragment, null)
        bindView(view)
        setupActivityToolbar(act, view)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                requireActivity().onBackPressed()
        }
        return true
    }

    private fun newListObserver(nameTextLayout: TextInputLayout) =
        Observer<Either<Exception, String>> { res ->
            when (res) {
                is Either.Left -> showError(nameTextLayout, res.value)
                is Either.Right -> openNewList(res.value)
            }
        }

    private fun bindView(view: View) {
        val nameTextLayout = view.findViewById<TextInputLayout>(R.id.name_text_layout)
        val nameEditText = view.findViewById<TextInputEditText>(R.id.name_edit_text)

        val submitButton = view.findViewById<View>(R.id.submit_btn)
        submitButton.setOnClickListener {
            val name = nameEditText.text!!.trim().toString()
            createListWithName(nameTextLayout, name)
        }

        repository.getNewListLiveData()
            .observe(this.viewLifecycleOwner, newListObserver(nameTextLayout))
    }


    private fun setupActivityToolbar(
        activity: AppCompatActivity,
        view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.create_new_list)

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun validateName(name: String) {
        if (name.isEmpty())
            throw UserFriendlyException(R.string.name_cant_be_empty)

        if (name.contains("/"))
            throw UserFriendlyException(R.string.forward_slash_not_allowed)
    }

    private fun createListWithName(nameTextLayout: TextInputLayout,
                                   name: String) {
        try {
            validateName(name)
            repository.createNewList(name)
        } catch (ex: UserFriendlyException) {
            nameTextLayout.error =
                ex.uiMessage.getText(requireContext())
        }
    }

    private fun showError(nameTextLayout: TextInputLayout,
                          exception: Exception) {
        val ctx = requireContext()
        if (exception is UserFriendlyException)
            nameTextLayout.error =
                exception.uiMessage.getText(ctx)
        else
            Toast.makeText(
                ctx,
                R.string.failed_to_create_list,
                Toast.LENGTH_SHORT).show()
    }

    private fun openNewList(name: String) {
        val dictSavedState = requireArguments()
            .getParcelable<DictSavedState>(DICT_SAVED_STATE_KEY)!!
        val nextFragment =
            StudyListFragment.newInstance(dictSavedState, name)
        requireFragmentManager()
            .runSlideTransition(nextFragment)
    }

    companion object {
        private const val DICT_SAVED_STATE_KEY = "dictSavedState"

        fun newInstance(savedState: DictSavedState): NewListFragment {
            val args = Bundle()
            args.putParcelable(DICT_SAVED_STATE_KEY, savedState)

            val f = NewListFragment()
            f.arguments = args
            return f
        }
    }
}

