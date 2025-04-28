package com.example.androidpracticumcustomview.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch


/*
Задание:
Реализуйте необходимые компоненты;
Создайте проверку что дочерних элементов не более 2-х;
Предусмотрите обработку ошибок рендера дочерних элементов.
Задание по желанию:
Предусмотрите параметризацию длительности анимации.
 */
@Composable
fun CustomContainerCompose(
    modifier: Modifier = Modifier,
    firstChild: @Composable (() -> Unit)?,
    secondChild: @Composable (() -> Unit)?,
    firstFadeInDurationMillis: Int = 2000,
    firstMoveDurationMillis: Int = 5000,
    secondFadeInDurationMillis: Int = 2000,
    secondMoveDurationMillis: Int = 5000
) {
    // Блок создания и инициализации переменных
    // ..
    var started by remember { mutableStateOf(false) }

    var firstStartX by remember { mutableStateOf(0f) }
    var firstTargetX by remember { mutableStateOf(0f) }
    var firstStartY by remember { mutableStateOf(0f) }
    var firstTargetY by remember { mutableStateOf(0f) }
    val firstOffsetAnimationX = remember { Animatable(0f) }
    val firstOffsetAnimationY = remember { Animatable(0f) }

    var secondStartX by remember { mutableStateOf(0f) }
    var secondTargetX by remember { mutableStateOf(0f) }
    var secondStartY by remember { mutableStateOf(0f) }
    var secondTargetY by remember { mutableStateOf(0f) }
    val secondOffsetAnimationX = remember { Animatable(0f) }
    val secondOffsetAnimationY = remember { Animatable(0f) }


    val coroutineScope = rememberCoroutineScope()

    // Блок активации анимации при первом запуске
    LaunchedEffect(
        firstStartX, firstTargetX, firstStartY, firstTargetY,
        secondStartX, secondTargetX, secondStartY, secondTargetY
    ) {
        coroutineScope.launch {
            firstOffsetAnimationX.snapTo(firstStartX)

            firstOffsetAnimationX.animateTo(
                firstTargetX,
                animationSpec = tween(durationMillis = firstMoveDurationMillis)
            )
        }
        coroutineScope.launch {
            firstOffsetAnimationY.snapTo(firstStartY)

            firstOffsetAnimationY.animateTo(
                firstTargetY,
                animationSpec = tween(durationMillis = firstMoveDurationMillis)
            )
        }
        coroutineScope.launch {
            secondOffsetAnimationX.snapTo(secondStartX)

            secondOffsetAnimationX.animateTo(
                secondTargetX,
                animationSpec = tween(durationMillis = secondMoveDurationMillis)
            )
        }
        coroutineScope.launch {
            secondOffsetAnimationY.snapTo(secondStartY)

            secondOffsetAnimationY.animateTo(
                secondTargetY,
                animationSpec = tween(durationMillis = secondMoveDurationMillis)
            )
        }
        started = true
    }

    // Основной контейнер
    Layout(
        content = {
            AnimatedVisibility(
                visible = started,
                enter = fadeIn(
                    animationSpec = tween(firstFadeInDurationMillis)
                )
            ) {
                if (firstChild != null) {
                    firstChild()
                }
            }

            AnimatedVisibility(
                visible = started,
                enter = fadeIn(
                    animationSpec = tween(secondFadeInDurationMillis)
                )
            ) {
                if (secondChild != null) {
                    secondChild()
                }
            }
        },
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            if (measurables.size > 2) {
                throw IllegalStateException()
            }

            val placeables = measurables.map { measurable ->
                measurable.measure(constraints)
            }

            val width = constraints.maxWidth
            val height = constraints.maxHeight

            layout(width, height) {
                placeables.forEachIndexed { index, placeable ->
                    val centerOffset = IntOffset(
                        (width / 2) - placeable.width / 2,
                        (height / 2) - placeable.height / 2
                    )
                    val position = when (index) {
                        0 -> {
                            val targetOffset = IntOffset((width - placeable.width) / 2, 0)

                            firstStartX = centerOffset.x.toFloat()
                            firstTargetX = targetOffset.x.toFloat()
                            firstStartY = centerOffset.y.toFloat()
                            firstTargetY = targetOffset.y.toFloat()

                            IntOffset(
                                firstOffsetAnimationX.value.toInt(),
                                firstOffsetAnimationY.value.toInt()
                            )
                        }

                        1 -> {
                            val targetOffset =
                                IntOffset((width - placeable.width) / 2, height - placeable.height)

                            secondStartX = centerOffset.x.toFloat()
                            secondTargetX = targetOffset.x.toFloat()
                            secondStartY = centerOffset.y.toFloat()
                            secondTargetY = targetOffset.y.toFloat()

                            IntOffset(
                                secondOffsetAnimationX.value.toInt(),
                                secondOffsetAnimationY.value.toInt()
                            )
                        }

                        else -> throw IllegalStateException()
                    }

                    placeable.place(position.x, position.y)
                }
            }
        }
    )
}