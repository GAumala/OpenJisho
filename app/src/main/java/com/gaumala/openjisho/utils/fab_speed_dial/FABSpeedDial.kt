package com.gaumala.openjisho.utils.fab_speed_dial

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.gaumala.openjisho.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton.SIZE_NORMAL


/**
 * An implementation of floating action button speed dial.
 *
 * https://material.io/components/buttons-floating-action-button/#types-of-transitions
 *
 * Must match its parent's dimensions and have the maximum
 * elevation among the children. Right after the view is created
 * you must call addItems() to configure the action cards and handle
 * click events.
 */
class FABSpeedDial: CoordinatorLayout {
    private val fabId = R.id.speed_dial_fab
    private val bgId = R.id.speed_dial_bg
    private val backgroundAlpha = 0.5f
    private val backgroundColorResId = android.R.color.black
    private val animationDuration = 150L
    private val animationInterval = 50L
    private val fabElevation = 4
    private val fabMargin = 16
    private val actionSpacing = 20
    private val actionTextSize = 14f
    private val actionTextVerticalPadding = 4
    private val actionTextHorizontalPadding = 8
    private val actionTextColorResId = android.R.color.black
    private val actionTextBgResId = R.drawable.speed_dial_action_label
    private val itemMinScale = 0.4f

    private lateinit var itemsLayout: ConstraintLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var background: View
    private lateinit var actionViewHolders: List<ActionViewHolder>

    private lateinit var listener: OnFABActionClickedListener
    private var lastClickedAction: FABAction? = null

    constructor(context: Context): super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initialize()
    }

    private val onBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            toggleSpeedDial()
        }
    }

    private fun dpToPx(dps: Int): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dps.toFloat(), resources.displayMetrics)

    private fun hideActions() {
        background.visibility = View.GONE
        actionViewHolders.forEach {
            it.button.visibility = View.GONE
            it.label.visibility = View.GONE
        }
    }

    private fun createButton(item: FABAction): View {
        val view = ImageView(context)
        view.id = item.id
        view.visibility = View.GONE
        view.elevation = dpToPx(fabElevation)
        val size = dpToPx(40).toInt()
        view.layoutParams = ConstraintLayout.LayoutParams(size, size)
        view.scaleType = ImageView.ScaleType.CENTER
        view.setBackgroundResource(R.drawable.speed_dial_action_button)
        view.setImageResource(item.iconResId)
        view.setOnClickListener {
            lastClickedAction = item
            toggleSpeedDial()
        }
        return view
    }

    private fun createLabel(item: FABAction): View {
        val view = TextView(context)
        view.id = View.generateViewId()
        view.visibility = View.GONE
        view.textSize = actionTextSize
        view.elevation = dpToPx(fabElevation)
        view.layoutParams = ConstraintLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        val xPadding = dpToPx(actionTextHorizontalPadding).toInt()
        val yPadding = dpToPx(actionTextVerticalPadding).toInt()
        view.setPadding(xPadding, yPadding, xPadding, yPadding)
        view.setText(item.textResId)
        view.setTypeface(view.typeface, Typeface.BOLD)
        view.setBackgroundResource(actionTextBgResId)
        view.setTextColor(ContextCompat.getColor(context, actionTextColorResId))
        view.setOnClickListener {
            lastClickedAction = item
            toggleSpeedDial()
        }
        return view
    }

    private fun createItemsLayoutParams(): LayoutParams {
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        lp.gravity = Gravity.BOTTOM or GravityCompat.END
        lp.dodgeInsetEdges = Gravity.BOTTOM
        return lp
    }

    private fun initialize() {
        background = View(context)
        background.id = bgId
        background.setBackgroundResource(backgroundColorResId)
        background.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        background.alpha = 0f
        addView(background)

        itemsLayout = ConstraintLayout(context)
        itemsLayout.layoutParams = createItemsLayoutParams()
        addView(itemsLayout)

        fab = FloatingActionButton(context)
        fab.id = fabId
        fab.size = SIZE_NORMAL
        fab.setBackgroundResource(R.color.main_blue)
        fab.supportImageTintList = ColorStateList.valueOf(Color.WHITE)
        fab.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        fab.setImageResource(R.drawable.ic_add_white_24dp)
        fab.setOnClickListener {
           toggleSpeedDial()
        }
        itemsLayout.addView(fab)

        val margin = dpToPx(fabMargin).toInt()
        val constraintSet = ConstraintSet()
        constraintSet.constrainHeight(
            fab.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainWidth(
            fab.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(
            fab.id, ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, margin)
        constraintSet.connect(
            fab.id, ConstraintSet.END,
            ConstraintSet.PARENT_ID, ConstraintSet.END, margin)

        constraintSet.applyTo(itemsLayout)
    }

    /**
     * This method must be called right after the view is created
     * to add the action cards and attach a click listener to each
     * of them.
     */
    fun addItems(items: List<FABAction>,
                 listener: OnFABActionClickedListener) {
        this.listener = listener

        val constraintSet = ConstraintSet()
        constraintSet.clone(itemsLayout)

        var prevId = fabId
        val spacing = dpToPx(actionSpacing).toInt()

        actionViewHolders = items.map{ fabMenuItem ->
            val button = createButton(fabMenuItem)
            val label = createLabel(fabMenuItem)

            itemsLayout.addView(button)
            itemsLayout.addView(label)

            constraintSet.constrainHeight(
                button.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.constrainWidth(
                button.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.connect(
                button.id, ConstraintSet.BOTTOM,
                prevId, ConstraintSet.TOP, spacing)
            constraintSet.connect(
                button.id, ConstraintSet.START,
                fabId, ConstraintSet.START)
            constraintSet.connect(
                button.id, ConstraintSet.END,
                fabId, ConstraintSet.END)

            constraintSet.constrainHeight(
                label.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.constrainWidth(
                label.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.connect(
                label.id, ConstraintSet.END,
                button.id, ConstraintSet.START, spacing)
            constraintSet.connect(
                label.id, ConstraintSet.TOP,
                button.id, ConstraintSet.TOP)
            constraintSet.connect(
                label.id, ConstraintSet.BOTTOM,
                button.id, ConstraintSet.BOTTOM)

            prevId = button.id

            ActionViewHolder(button, label)
        }
        constraintSet.applyTo(itemsLayout)
        // I have no idea why I have to hide everything
        // again after setting constraints
        hideActions()

    }

    private fun createActionInterpolator(isOpening: Boolean): TimeInterpolator =
        if (isOpening) DecelerateInterpolator()
        else AccelerateInterpolator()

    private fun createActionAnimators(itemView: View,
                                      startDelay: Long,
                                      isOpening: Boolean): List<ObjectAnimator> {
        val startAlpha = if (isOpening) 0f else 1f
        val endAlpha = if (isOpening) 1f else 0f
        val startScale = if (isOpening) itemMinScale else 1f
        val endScale = if (isOpening) 1f else itemMinScale

        itemView.visibility = View.VISIBLE
        itemView.alpha = startAlpha
        itemView.scaleY = startScale
        itemView.scaleX = startScale

        return listOf(
            ObjectAnimator.ofFloat(itemView, "alpha", startAlpha, endAlpha).apply {
                duration = animationDuration
                interpolator = createActionInterpolator(isOpening)
                setStartDelay(startDelay)
            },
             ObjectAnimator.ofFloat(itemView, "scaleX", startScale, endScale).apply {
                duration = animationDuration
                interpolator = createActionInterpolator(isOpening)
                setStartDelay(startDelay)
            },
            ObjectAnimator.ofFloat(itemView, "scaleY", startScale, endScale).apply {
                duration = animationDuration
                interpolator = createActionInterpolator(isOpening)
                setStartDelay(startDelay)
            }
        )
    }

    private fun createChildrenAnimators(isOpening: Boolean): List<ObjectAnimator> {
        background.visibility = View.VISIBLE
        val startAngle = if (isOpening)  0f else -45f
        val endAngle = if (isOpening)  -45f else -0f
        val startAlpha = if (isOpening)  0f else backgroundAlpha
        val endAlpha = if (isOpening)  backgroundAlpha else 0f
        return listOf(
            ObjectAnimator.ofFloat(fab, "rotation", startAngle, endAngle).apply {
                duration = animationDuration
                interpolator = OvershootInterpolator()
            },
            ObjectAnimator.ofFloat(background, "alpha", startAlpha, endAlpha)
                .setDuration(animationDuration)
        )
    }

    private var state = State.closed

    private fun toggleSpeedDial() {
        if (state == State.locked)
            return

        val isOpening = state == State.closed

        val animatorSet = AnimatorSet()
        var startDelay = animationInterval
        val holders = if (isOpening) actionViewHolders
                      else actionViewHolders.reversed()
        val itemAnimators = holders.flatMap {
            val animators = createActionAnimators(it.button, startDelay, isOpening)
                .plus(createActionAnimators(it.label, startDelay, isOpening))
            startDelay += animationInterval
            animators
        }

        val animators = itemAnimators.plus(createChildrenAnimators(isOpening))

        animatorSet.addListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                state = if (isOpening) State.open else State.closed
                onTransitionEnd()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                state = State.locked
            }

        })
        animatorSet.playTogether(animators)
        animatorSet.start()
    }

    private fun onTransitionEnd() {
        if (state == State.closed) {
            hideActions()
            disableOnBackPressedCallback()
            dispatchLastClickedAction()
        } else {
            lastClickedAction = null
            enableOnBackPressedCallback()
        }
    }

    private fun dispatchLastClickedAction() {
        val action = lastClickedAction
        if (action != null) {
            fab.post {
                listener.onFABActionClicked(action)
            }
            lastClickedAction = null
        }
    }

    private fun enableOnBackPressedCallback() {
        val activity = context as? ComponentActivity ?: return
        activity.onBackPressedDispatcher
            .addCallback(onBackPressedCallback)
    }

    private fun disableOnBackPressedCallback() {
        onBackPressedCallback.remove()
    }

    private class ActionViewHolder(val button: View, val label: View)

    private enum class State {
        open, locked, closed
    }

}