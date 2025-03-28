package com.example.noteapp.presentation.ui.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> CircularPicker(items: List<T>, selectedItem: T, onItemSelected: (T) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val itemCount = items.size
    val infiniteItemCount = Int.MAX_VALUE // Simulate infinite list
    val midPoint = infiniteItemCount / 2 - (infiniteItemCount / 2) % itemCount // Center the list
    val initialIndex = midPoint + items.indexOf(selectedItem)

    // Scroll to initial position
    LaunchedEffect(Unit) {
        listState.scrollToItem(initialIndex)
    }

    Box(
        modifier = Modifier
            .height(120.dp)
            .width(80.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(infiniteItemCount) { index ->
                val realIndex = index % itemCount // Circular indexing
                val item = items[realIndex]
                val isSelected = index == listState.firstVisibleItemIndex

                Text(
                    text = item.toString(),
                    fontSize = if (isSelected) 24.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF6A1B9A) else Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        //.align(Alignment.Center)
                        .clickable {
                            coroutineScope.launch { listState.animateScrollToItem(index) }
                            onItemSelected(item)
                        }
                )
            }
        }
    }

    // Auto-update selected item when scrolling
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { centerIndex ->
                val newIndex = centerIndex % itemCount
                onItemSelected(items[newIndex])
                Log.d("CustomDatePicker", "Selected item: ${items[newIndex]} at index $centerIndex")
            }
    }
}