package com.gaumala.openjisho.frontend.navigation

import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionSet
import com.gaumala.openjisho.R
import com.gaumala.openjisho.frontend.dict.DictFragment
import com.gaumala.openjisho.frontend.radicals.RadicalsFragment

fun FragmentManager.runSlideTransition(
    newFragment: Fragment,
    reverse: Boolean = false,
    addToBackStack: Boolean = false
) {
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

private val dictTopTargets = listOf(
    R.id.dict_app_bar,
)
private val dictBottomTargets = listOf(
    R.id.pager,
    R.id.speed_dial_fab,
)

private val radicalsTopTargets = listOf(
    R.id.radicals_app_bar
)
private val radicalsBottomTargets = listOf(
    R.id.results_recycler,
    R.id.radicals_recycler,
    R.id.welcome_text,
    R.id.welcome_art,
)

private val sentenceTopTargets = listOf(
    R.id.sentence_app_bar
)
private val sentenceBottomTargets = listOf(
    R.id.recycler,
    R.id.welcome_us_text,
    R.id.welcome_us_art,
)
private const val slideUpDownDuration = 300L

private fun createSlideUpDownExitTransition(
    topTargets: List<Int>,
    bottomTargets: List<Int>
): Transition {
    val exitTransitionSet = TransitionSet()
    val slideOutDown = Slide(Gravity.BOTTOM)
    slideOutDown.duration = slideUpDownDuration
    bottomTargets.forEach {
        slideOutDown.addTarget(it)
    }
    exitTransitionSet.addTransition(slideOutDown)

    val slideOutUp = Slide(Gravity.TOP)
    slideOutUp.duration = slideUpDownDuration
    topTargets.forEach {
        slideOutUp.addTarget(it)
    }
    exitTransitionSet.addTransition(slideOutUp)
    return exitTransitionSet
}

private fun createSlideUpDownEnterTransition(
    topTargets: List<Int>,
    bottomTargets: List<Int>
): Transition {
    val enterTransitionSet = TransitionSet()

    val slideInDown = Slide(Gravity.TOP)
    topTargets.forEach {
        slideInDown.addTarget(it)
    }
    slideInDown.duration = slideUpDownDuration
    enterTransitionSet.addTransition(slideInDown)

    val slideInUp = Slide(Gravity.BOTTOM)
    bottomTargets.forEach {
        slideInUp.addTarget(it)
    }
    slideInUp.duration = slideUpDownDuration
    enterTransitionSet.addTransition(slideInUp)
    enterTransitionSet.startDelay = slideUpDownDuration

    return enterTransitionSet
}

fun FragmentManager.runEnterRadicalSearchTransition(
    prevFragment: Fragment,
    nextFragment: RadicalsFragment,
    exitBottomTargets: List<Int>,
) {
    prevFragment.exitTransition =
        if (prevFragment is DictFragment) {
            createSlideUpDownExitTransition(dictTopTargets, exitBottomTargets)
        } else {
            createSlideUpDownExitTransition(sentenceTopTargets, exitBottomTargets)
        }
    nextFragment.enterTransition =
        createSlideUpDownEnterTransition(radicalsTopTargets, radicalsBottomTargets)

    val arrayList = ArrayList(exitBottomTargets)
    nextFragment.requireArguments()
        .putIntegerArrayList(RadicalsFragment.PREV_SCREEN_BOTTOM_TARGETS_KEY, arrayList)

    beginTransaction()
        .replace(R.id.container, nextFragment)
        .commitAllowingStateLoss()
}

fun FragmentManager.runExitRadicalSearchTransition(
    prevFragment: RadicalsFragment,
    nextFragment: Fragment,
    exitBottomTargets: List<Int>,
    enterBottomTargets: List<Int>,
) {
    prevFragment.exitTransition =
        createSlideUpDownExitTransition(radicalsTopTargets, exitBottomTargets)
    nextFragment.enterTransition =
        if (nextFragment is DictFragment) {
            createSlideUpDownEnterTransition(dictTopTargets, enterBottomTargets)
        } else {
            createSlideUpDownEnterTransition(sentenceTopTargets, enterBottomTargets)
        }
    Log.d("SentenceDebug", "enter bottom $enterBottomTargets")

    beginTransaction()
        .replace(R.id.container, nextFragment)
        .commitAllowingStateLoss()
}