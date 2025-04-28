package com.example.androidpracticumcustomview.ui.theme

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/*
Задание:
Реализуйте необходимые компоненты;
Создайте проверку что дочерних элементов не более 2-х;
Предусмотрите обработку ошибок рендера дочерних элементов.
Задание по желанию:
Предусмотрите параметризацию длительности анимации.
 */

class CustomContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val firstChild: View?
        get() = if (childCount > 0) getChildAt(0) else null
    private val secondChild: View?
        get() = if (childCount > 1) getChildAt(1) else null

    private val firstAlphaAnimator = ObjectAnimator().apply {
        setPropertyName("alpha")
        setFloatValues(0f, 1f)
        duration = 2000
    }
    private val firstPositionAnimator = ValueAnimator().apply {
        duration = 5000
        addUpdateListener { animation ->
            firstChild?.translationY = animation.animatedValue as Float
        }
    }

    private val secondAlphaAnimator = ObjectAnimator().apply {
        setPropertyName("alpha")
        setFloatValues(0f, 1f)
        duration = 2000
    }
    private val secondPositionAnimator = ValueAnimator().apply {
        duration = 5000
        addUpdateListener { animation ->
            secondChild?.translationY = animation.animatedValue as Float
        }
    }

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        checkChildCount()

        firstChild?.let { measureChildView(it, widthMeasureSpec, heightMeasureSpec) }
        secondChild?.let { measureChildView(it, widthMeasureSpec, heightMeasureSpec) }

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    private fun measureChildView(child: View, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val childWidthSpec =
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST)
        val childHeightSpec =
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)

        child.measure(childWidthSpec, childHeightSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val firstChildLeft = ((right - left) / 2) - (firstChild?.measuredWidth?.div(2) ?: 0)
        firstChild?.layout(
            firstChildLeft,
            top,
            firstChildLeft + (firstChild?.measuredWidth ?: 0),
            top + (firstChild?.measuredHeight ?: 0)
        )
        if (!firstPositionAnimator.isStarted && childCount == 1) {
            val firstPositionAnimationStart =
                ((bottom - top) / 2) - (firstChild?.measuredHeight?.div(2) ?: 0)
            firstPositionAnimator.apply {
                setFloatValues(firstPositionAnimationStart.toFloat(), top.toFloat())
                start()
            }
        }

        val secondChildLeft = ((right - left) / 2) - (secondChild?.measuredWidth?.div(2) ?: 0)
        secondChild?.layout(
            secondChildLeft,
            bottom - (secondChild?.measuredHeight ?: 0),
            secondChildLeft + (secondChild?.measuredWidth ?: 0),
            bottom
        )
        if (!secondPositionAnimator.isStarted && childCount == 2) {
            val secondPositionAnimationStart =
                ((top - bottom) / 2) + (secondChild?.measuredHeight?.div(2) ?: 0)
            secondPositionAnimator.apply {
                setFloatValues(secondPositionAnimationStart.toFloat(), top.toFloat())
                start()
            }
        }
    }

    override fun addView(child: View) {
        checkChildCount()

        super.addView(child)

        if (childCount == 1) {
            firstAlphaAnimator.target = firstChild
            firstAlphaAnimator.start()
        }

        if (childCount == 2) {
            secondAlphaAnimator.target = secondChild
            secondAlphaAnimator.start()
        }
    }

    private fun checkChildCount() {
        if (childCount > 2) throw IllegalStateException()
    }
}