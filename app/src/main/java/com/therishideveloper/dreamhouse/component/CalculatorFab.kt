package com.therishideveloper.dreamhouse.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.therishideveloper.dreamhouse.ui.theme.tealColor

@Composable
fun CalculatorFab(
    lazyListState: LazyListState? = null,
    isListEmpty: Boolean = true
) {
    var calcExpr by remember { mutableStateOf("") }
    var calcRes by remember { mutableStateOf("0") }
    var showCalculator by remember { mutableStateOf(false) }

//    val isFabVisible by remember(notes) {
//        derivedStateOf {
//            val layoutInfo = lazyListState.layoutInfo
//            val visibleItems = layoutInfo.visibleItemsInfo
//
//            if (visibleItems.isEmpty()) {
//                true
//            } else {
//                val lastItem = visibleItems.lastOrNull()
//                val totalItemsCount = layoutInfo.totalItemsCount
//
//                if (lastItem != null && lastItem.index == totalItemsCount - 1) {
//                    val viewportEnd = layoutInfo.viewportEndOffset
//                    val fabTopBoundary = viewportEnd - 250 // বাটনের উপরের সীমানা
//                    val fabBottomBoundary = viewportEnd - 50 // বাটনের নিচের সীমানা
//                    val itemTop = lastItem.offset
//                    val itemBottom = lastItem.offset + lastItem.size
//                    val isOverlapping = itemBottom > fabTopBoundary && itemTop < fabBottomBoundary
//
//                    !isOverlapping
//                } else {
//                    true
//                }
//            }
//        }
//    }

    val isFabVisible by remember(lazyListState, isListEmpty) {
        derivedStateOf {
            if (lazyListState == null || isListEmpty) true
            else {
                val layoutInfo = lazyListState.layoutInfo
                val visibleItems = layoutInfo.visibleItemsInfo
                val lastItem = visibleItems.lastOrNull()
                val totalItemsCount = layoutInfo.totalItemsCount

                if (lastItem != null && lastItem.index == totalItemsCount - 1) {
                    val viewportEnd = layoutInfo.viewportEndOffset
                    val fabTopBoundary = viewportEnd - 250
                    val itemBottom = lastItem.offset + lastItem.size
                    itemBottom <= fabTopBoundary
                } else true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isFabVisible,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { showCalculator = true },
                containerColor = tealColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Calculate, null, modifier = Modifier.size(30.dp))
            }
        }
    }

    if (showCalculator) {
        CalculatorDialog(
            initialExpression = calcExpr,
            initialResult = calcRes,
            onMinimize = { e, r -> calcExpr = e; calcRes = r; showCalculator = false },
            onClose = { calcExpr = ""; calcRes = "0"; showCalculator = false }
        )
    }
}
