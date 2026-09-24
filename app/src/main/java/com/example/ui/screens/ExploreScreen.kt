package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Opportunity
import com.example.model.OpportunityCategory
import com.example.ui.components.OpportunityCard
import com.example.ui.theme.CampusPrimary

@Composable
fun ExploreScreen(
    opportunities: List<Opportunity>,
    savedIds: Set<String>,
    onOpportunityClick: (Opportunity) -> Unit,
    onSaveToggle: (Opportunity) -> Unit,
    initialCategory: OpportunityCategory = OpportunityCategory.ALL,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var isRemoteOnly by remember { mutableStateOf(false) }
    var isPaidOnly by remember { mutableStateOf(false) }
    var isUrgentOnly by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filteredOpportunities = remember(
        opportunities,
        searchQuery,
        selectedCategory,
        isRemoteOnly,
        isPaidOnly,
        isUrgentOnly
    ) {
        opportunities.filter { opp ->
            val matchesCategory = selectedCategory == OpportunityCategory.ALL || opp.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                opp.title.contains(searchQuery, ignoreCase = true) ||
                opp.organization.contains(searchQuery, ignoreCase = true) ||
                opp.location.contains(searchQuery, ignoreCase = true) ||
                opp.requiredSkills.any { it.contains(searchQuery, ignoreCase = true) }
            val matchesRemote = !isRemoteOnly || opp.isRemote
            val matchesPaid = !isPaidOnly || opp.isPaid
            val matchesUrgent = !isUrgentOnly || opp.daysRemaining <= 7

            matchesCategory && matchesQuery && matchesRemote && matchesPaid && matchesUrgent
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen")
    ) {
        // Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_input"),
                placeholder = { Text("Search internships, hackathons, skills...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = CampusPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CampusPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Horizontal Scrollable Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OpportunityCategory.entries.forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CampusPrimary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Filter Quick Chips (Remote, Paid, Urgent)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = isRemoteOnly,
                    onClick = { isRemoteOnly = !isRemoteOnly },
                    label = { Text("🏠 Remote") },
                    shape = RoundedCornerShape(8.dp)
                )

                FilterChip(
                    selected = isPaidOnly,
                    onClick = { isPaidOnly = !isPaidOnly },
                    label = { Text("💰 Stipend / Paid") },
                    shape = RoundedCornerShape(8.dp)
                )

                FilterChip(
                    selected = isUrgentOnly,
                    onClick = { isUrgentOnly = !isUrgentOnly },
                    label = { Text("⚡ Urgent (< 7d)") },
                    shape = RoundedCornerShape(8.dp)
                )

                if (isRemoteOnly || isPaidOnly || isUrgentOnly || searchQuery.isNotEmpty() || selectedCategory != OpportunityCategory.ALL) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelMedium.copy(color = CampusPrimary, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickable {
                                searchQuery = ""
                                selectedCategory = OpportunityCategory.ALL
                                isRemoteOnly = false
                                isPaidOnly = false
                                isUrgentOnly = false
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Results count label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredOpportunities.size} opportunities found",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // List of Opportunities
        if (filteredOpportunities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "No results",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No opportunities match your filter",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing your search query or selecting a different category.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        searchQuery = ""
                        selectedCategory = OpportunityCategory.ALL
                        isRemoteOnly = false
                        isPaidOnly = false
                        isUrgentOnly = false
                    }) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOpportunities, key = { it.id }) { opp ->
                    OpportunityCard(
                        opportunity = opp,
                        isSaved = savedIds.contains(opp.id),
                        onSaveClick = { onSaveToggle(opp) },
                        onClick = { onOpportunityClick(opp) }
                    )
                }
            }
        }
    }
}
