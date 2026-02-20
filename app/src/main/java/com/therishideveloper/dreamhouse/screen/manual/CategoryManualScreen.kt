package com.therishideveloper.dreamhouse.screen.manual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.CurrentBalance
import com.therishideveloper.dreamhouse.ui.theme.tealColor
import com.therishideveloper.dreamhouse.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManualScreen(
    onMenuClick: () -> Unit,
    viewModel: TransactionViewModel
) {
    val currentBalance by viewModel.currentBalance.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cat_guide), color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tealColor),
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            "Menu",
                            tint = Color.White
                        )
                    }
                },
                actions = { CurrentBalance(currentBalance.toString()) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // সকল সেকশনের নাম সংগ্রহ করুন
            val sections = Category.getAllSections() // Category enum এ এই ফাংশনটি যোগ করতে হবে

            sections.forEach { sectionName ->
                stickyHeader {
                    CategorySectionHeader(sectionName = sectionName)
                }
                val categoriesInSection = Category.getCategoriesBySection(sectionName)
                items(categoriesInSection) { category ->
                    CategoryItem(category = category)
                }
            }
        }
    }
}

@Composable
fun CategorySectionHeader(sectionName: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = tealColor.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = when (sectionName) {
                "INCOME" -> stringResource(id = R.string.section_income)
                "STRUCTURAL" -> stringResource(id = R.string.section_structural)
                "LABOR" -> stringResource(id = R.string.section_labor)
                "FINISHING" -> stringResource(id = R.string.section_finishing)
                "MISCELLANEOUS" -> stringResource(id = R.string.section_miscellaneous)
                else -> sectionName
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun CategoryItem(category: Category) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        tealColor.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = tealColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = stringResource(id = category.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = category.descriptionRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}