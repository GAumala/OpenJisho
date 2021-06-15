package com.gaumala.openjisho.frontend.dict

import android.os.Handler
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.navigation.MainScreen
import com.gaumala.openjisho.frontend.navigation.Navigator
import com.gaumala.openjisho.utils.hideKeyboard
import com.google.android.material.navigation.NavigationView

class NavigationDrawer(private val activity: AppCompatActivity) {
    private val handler = Handler()
    private val navigator = activity as Navigator
    private val drawer = activity.findViewById<DrawerLayout>(R.id.drawer_layout)
    private val navigationView = activity.findViewById<NavigationView>(R.id.nav_view)

    private val drawerGravity = GravityCompat.START
    private val currentFragment: Fragment?
        get() = activity.supportFragmentManager.findFragmentById(R.id.container)
    private var selectedMenuItemId = -1

    init {
        setupNavigationDrawer()
    }

    private val onBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            drawer.closeDrawer(drawerGravity)
        }
    }

    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener {item ->
            selectedMenuItemId = item.itemId
            drawer.closeDrawer(drawerGravity)

            true
        }

        drawer.addDrawerListener(object: DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                onBackPressedCallback.remove()
                handleSelectedMenuItem()
            }

            override fun onDrawerOpened(drawerView: View) {
                activity.onBackPressedDispatcher
                    .addCallback(onBackPressedCallback)
                drawer.hideKeyboard()
            }

        })

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun handleSelectedMenuItem() {
        when (selectedMenuItemId) {
            R.id.new_search ->
                showDictFragment()
            R.id.rebuild ->
                showRebuildDatabaseDialog()
            R.id.about ->
                showAboutFragment()
            R.id.help ->
                showTourFragment()
            R.id.my_lists ->
                showMyListsFragment()
        }

        selectedMenuItemId = -1
    }

    private fun showAboutFragment() {
        // apparently by using post, the keyboard hides
        // automatically after a fragment transition? idk
        handler.post {
            navigator.goTo(MainScreen.About)
        }
    }

    private fun showTourFragment() {
        handler.post {
            navigator.goTo(MainScreen.Tour(false))
        }
    }

    private fun showRebuildDatabaseDialog() {
        handler.post {
            RebuildDialog.Manager(activity.supportFragmentManager)
                .prompt()
        }
    }

    private fun showMyListsFragment() {
        handler.post {
            val fragment = currentFragment
            if (fragment is DictFragment) {
                val savedState = fragment.getSavedState()
                navigator.goTo(MainScreen.MyLists(savedState))
            }
        }
    }

    private fun showDictFragment() {
        handler.post {
            val fragment = currentFragment
            if (fragment !is DictFragment)
                navigator.goTo(MainScreen.Dictionary())
        }
    }

    fun open() {
        drawer.openDrawer(drawerGravity)
    }

    fun setDrawerLocked(isLocked: Boolean) {
        drawer.setDrawerLockMode(
            if (isLocked) DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            else DrawerLayout.LOCK_MODE_UNLOCKED)
    }

}