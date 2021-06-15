package com.gaumala.openjisho

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gaumala.openjisho.backend.keyvalue.KeyValueStorage
import com.gaumala.openjisho.backend.keyvalue.SharedPrefsStorage
import com.gaumala.openjisho.backend.setup.SetupService
import com.gaumala.openjisho.frontend.dict.DictFragment
import com.gaumala.openjisho.frontend.dict.NavigationDrawer
import com.gaumala.openjisho.frontend.navigation.*
import com.gaumala.openjisho.frontend.study_list.StudyListFragment
import com.gaumala.openjisho.frontend.pages.WelcomeFragment
import com.gaumala.openjisho.frontend.setup.SetupFragment
import com.gaumala.openjisho.frontend.my_lists.MyListsFragment
import com.gaumala.openjisho.frontend.tour.TourFragment

/**
 * Main application activity that handles most of the fragments.
 * It's main responsibility is to provide navigation between fragments so
 * it implements the [Navigator] interface with [MainScreen] as type parameter.
 */
class MainActivity : AppCompatActivity(),
    Navigator, NavDrawerContainer {

    private val kvStorage: KeyValueStorage by lazy {
        SharedPrefsStorage(this)
    }
    private lateinit var navDrawer: NavigationDrawer

    private val fragmentAlreadySet
        get() = supportFragmentManager.findFragmentById(R.id.container) != null

    private val isDBReady
        get() = kvStorage.getBoolean(KeyValueStorage.Key.dbSetup, false)

    private val isRunningSetup
        get() = SetupService.isRunning

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        navDrawer = NavigationDrawer(this)

        when {
            fragmentAlreadySet -> return // fragment has already been set
            isDBReady -> createActivityAsUsual()
            isRunningSetup -> createActivityWithSetup()
            else -> createActivityWithWelcome()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null)
            return

        when (SecondaryScreen.fromRequestCode(requestCode)) {
            SecondaryScreen.pickDictEntry,
            SecondaryScreen.composeText ->
                forwardResultToStudyListFragment(data)
            SecondaryScreen.showEntry,
            SecondaryScreen.showAppInfo,
            SecondaryScreen.showText -> TODO()
        }
    }

    private fun forwardResultToStudyListFragment(data: Intent) {
        val f = supportFragmentManager.findFragmentById(R.id.container)
                as? StudyListFragment ?: return

        f.processResult(data)
    }

    fun openSecondaryActivity(screen: SecondaryScreen,
                              extraArgs: Bundle = Bundle()) {
        val intent = Intent(this,
            SecondaryActivity::class.java)
        intent.putExtra(SecondaryActivity.SCREEN_KEY, screen.toScreenKey())
        intent.putExtra(SecondaryActivity.EXTRA_ARGS_KEY, extraArgs)

        startActivityForResult(intent, screen.toRequestCode())
    }

    private fun createActivityAsUsual() {
        supportFragmentManager.beginTransaction()
        .replace(R.id.container, DictFragment.newInstance())
        .commitNow()
    }

    private fun createActivityWithSetup() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SetupFragment.newInstance())
            .commitNow()
    }

    private fun createActivityWithWelcome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, WelcomeFragment.newInstance())
            .commitNow()
    }

    private fun goToDictionary(args: MainScreen.Dictionary) {
        val nextFragment = DictFragment.newInstance(
            savedState = args.savedState,
            delayKeyboardBy = 300,
            isPicker = false)

        supportFragmentManager.runSlideTransition(
            newFragment = nextFragment,
            reverse = args.reverse)
    }


    private fun goToSetup() {
        SharedPrefsStorage(this)
            .putBoolean(KeyValueStorage.Key.dbSetup, false)
        supportFragmentManager.runSlideTransition(
            SetupFragment.newInstance())
    }

    private fun goToAbout() {
        openSecondaryActivity(SecondaryScreen.showAppInfo)
    }

    private fun goToTour(args: MainScreen.Tour) {
        if (args.isRunningSetup)
            supportFragmentManager.runSlideTransition(
                newFragment = TourFragment.newInstance(true),
                addToBackStack = false)
        else {
            val extraArgs = Bundle()
            extraArgs.putBoolean(TourFragment.IS_RUNNING_SETUP_KEY, false)
            openSecondaryActivity(SecondaryScreen.showHelp, extraArgs)
        }
    }

    private fun goToMyLists(args: MainScreen.MyLists) {
        supportFragmentManager.runSlideTransition(
            newFragment = MyListsFragment.newInstance(args.savedState),
            reverse = args.reverse)
    }

    override fun goTo(screen: MainScreen) {
        when (screen) {
            MainScreen.Setup -> goToSetup()
            MainScreen.About -> goToAbout()
            is MainScreen.Tour -> goToTour(screen)
            is MainScreen.Dictionary -> goToDictionary(screen)
            is MainScreen.MyLists -> goToMyLists(screen)
        }
    }

    override fun openDrawer() {
        navDrawer.open()
    }

    override fun setDrawerLocked(isLocked: Boolean) {
        navDrawer.setDrawerLocked(isLocked)
    }
}
