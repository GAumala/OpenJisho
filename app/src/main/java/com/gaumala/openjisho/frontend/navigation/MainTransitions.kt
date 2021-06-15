package com.gaumala.openjisho.frontend.navigation

import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.dict.DictFragment
import com.gaumala.openjisho.frontend.radicals.RadicalsFragment

fun FragmentManager.runSlideTransition(newFragment: Fragment,
                                       reverse: Boolean = false,
                                       addToBackStack: Boolean = false) {
    beginTransaction().apply {
        if (reverse)
            setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
                R.anim.enter_from_right,
                R.anim.exit_to_left
            )
        else
            setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
        replace(R.id.container, newFragment)
        if (addToBackStack)
            addToBackStack(null)
        commit()
    }
}

fun FragmentManager.runDictToRadicalsTransition(
    prevFragment: DictFragment,
    nextFragment: RadicalsFragment) {

    val moveDuration = 300L

    val exitTransitionSet = TransitionSet()
    prevFragment.exitTransition = exitTransitionSet

    val slideOutDown = Slide(Gravity.BOTTOM)
    slideOutDown.duration = moveDuration
    slideOutDown.addTarget(R.id.pager)
    slideOutDown.addTarget(R.id.speed_dial_fab)
    exitTransitionSet.addTransition(slideOutDown)

    val slideOutUp = Slide(Gravity.TOP)
    slideOutUp.duration = moveDuration
    slideOutUp.addTarget(R.id.dict_app_bar)
    exitTransitionSet.addTransition(slideOutUp)

    val enterTransitionSet = TransitionSet()
    nextFragment.enterTransition = enterTransitionSet

    val slideInDown = Slide(Gravity.TOP)
    slideInDown.addTarget(R.id.radicals_app_bar)
    slideInDown.duration = moveDuration
    enterTransitionSet.addTransition(slideInDown)

    val slideInUp = Slide(Gravity.BOTTOM)
    slideInUp.addTarget(R.id.results_recycler)
    slideInUp.addTarget(R.id.radicals_recycler)
    slideInUp.addTarget(R.id.welcome_text)
    slideInUp.addTarget(R.id.welcome_art)
    slideInUp.duration = moveDuration
    enterTransitionSet.addTransition(slideInUp)
    enterTransitionSet.startDelay = moveDuration

    beginTransaction()
        .replace(R.id.container, nextFragment)
        .commitAllowingStateLoss()
}

fun FragmentManager.runRadicalsToDictTransition(
    prevFragment: RadicalsFragment,
    nextFragment: DictFragment) {

    val moveDuration = 300L

    val enterTransitionSet = TransitionSet()
    nextFragment.enterTransition = enterTransitionSet

    val slideInUp = Slide(Gravity.BOTTOM)
    slideInUp.duration = moveDuration
    slideInUp.addTarget(R.id.pager)
    slideInUp.addTarget(R.id.speed_dial_fab)
    enterTransitionSet.addTransition(slideInUp)

    val slideInDown = Slide(Gravity.TOP)
    slideInDown.duration = moveDuration
    slideInDown.addTarget(R.id.dict_app_bar)
    enterTransitionSet.addTransition(slideInDown)
    enterTransitionSet.startDelay = moveDuration

    val exitTransitionSet = TransitionSet()
    prevFragment.exitTransition = exitTransitionSet

    val slideOutUp = Slide(Gravity.TOP)
    slideOutUp.addTarget(R.id.radicals_app_bar)
    slideOutUp.duration = moveDuration
    exitTransitionSet.addTransition(slideOutUp)

    val slideOutDown = Slide(Gravity.BOTTOM)
    slideOutDown.addTarget(R.id.results_recycler)
    slideOutDown.addTarget(R.id.radicals_recycler)
    slideOutDown.addTarget(R.id.welcome_text)
    slideOutDown.addTarget(R.id.welcome_art)
    slideOutDown.duration = moveDuration
    exitTransitionSet.addTransition(slideOutDown)

    beginTransaction()
        .replace(R.id.container, nextFragment)
        .commitAllowingStateLoss()
}

