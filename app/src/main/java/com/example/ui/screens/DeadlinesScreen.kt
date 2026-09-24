package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.Opportunity
import com.example.ui.theme.CampusAccent
import com.example.ui.theme.CampusPrimary
import com.example.ui.theme.CampusUrgent
import com.example.ui.theme.CampusWarning

@Composable
fun DeadlinesScreen(
    opportunities: List<Opportunity>,
    savedIds: Set<String>,
    onOpportunityClick: (Opportunity) -> Unit,
    onApplyClick: (Opportunity) -> Unit,
    modifier: Modifier = Modifier
) {
    val todayAndTomorrow = opportunities.filter { it.daysRemaining <= 1 }.sortedBy { it.daysRemaining }
    val thisWeek = opportunities.filter { it.daysRemaining in 2..7 }.sortedBy { it.daysRemaining }
    val later = opportunities.filter { it.daysRemaining > 7 }.sortedBy { it.daysRemaining }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("deadlines_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Deadlines",
                        tint = CampusPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upcoming Deadlines Calendar",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track time-critical submission windows across all categories",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section 1: Today & Tomorrow
        if (todayAndTomorrow.isNotEmpty()) {
            item {
                DeadlineSectionHeader(
                    title = "Urgent: Today & Tomorrow",
                    count = todayAndTomorrow.size,
                    color = CampusUrgent,
                    icon = Icons.Default.Warning
                )
            }

            items(todayAndTomorrow, key = { it.id }) { opp ->
                DeadlineOpportunityRow(
                    opportunity = opp,
                    urgencyColor = CampusUrgent,
                    isSaved = savedIds.contains(opp.id),
                    onClick = { onOpportunityClick(opp) },
                    onApply = { onApplyClick(opp) }
                )
            }
        }

        // Section 2: This Week
        if (thisWeek.isNotEmpty()) {
            item {
                DeadlineSectionHeader(
                    title = "This Week (< 7 Days)",
                    count = thisWeek.size,
                    color = CampusWarning,
                    icon = Icons.Default.CalendarToday
                )
            }

            items(thisWeek, key = { it.id }) { opp ->
                DeadlineOpportunityRow(
                    opportunity = opp,
                    urgencyColor = CampusWarning,
                    isSaved = savedIds.contains(opp.id),
                    onClick = { onOpportunityClick(opp) },
                    onApply = { onApplyClick(opp) }
                )
            }
        }

        // Section 3: Later
        if (later.isNotEmpty()) {
            item {
                DeadlineSectionHeader(
                    title = "Later This Month",
                    count = later.size,
                    color = CampusAccent,
                    icon = Icons.Default.CalendarToday
                )
            }

            items(later, key = { it.id }) { opp ->
                DeadlineOpportunityRow(
                    opportunity = opp,
                    urgencyColor = CampusAccent,
                    isSaved = savedIds.contains(opp.id),
                    onClick = { onOpportunityClick(opp) },
                    onApply = { onApplyClick(opp) }
                )
            }
        }
    }
}

@Composable
fun DeadlineSectionHeader(
    title: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Surface(
            color = color.copy(alpha = 0.15f),
            shape = CircleShape,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Spacer(modifier = Modifier.width(6.dp))
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun DeadlineOpportunityRow(
    opportunity: Opportunity,
    urgencyColor: Color,
    isSaved: Boolean,
    onClick: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("deadline_item_${opportunity.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Countdown badge
            Surface(
                color = urgencyColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${opportunity.daysRemaining}d",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = urgencyColor
                    )
                    Text(
                        text = "left",
                        style = MaterialTheme.typography.labelSmall,
                        color = urgencyColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = opportunity.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = opportunity.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${opportunity.organization} • Deadline: ${opportunity.deadline}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Apply Button
            Button(
                onClick = onApply,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CampusPrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Apply", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
