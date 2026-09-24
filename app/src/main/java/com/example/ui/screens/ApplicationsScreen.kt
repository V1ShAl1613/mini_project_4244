package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ApplicationItem
import com.example.model.ApplicationStatus
import com.example.ui.theme.CampusAccent
import com.example.ui.theme.CampusPrimary
import com.example.ui.theme.CampusUrgent
import com.example.ui.theme.CampusWarning

@Composable
fun ApplicationsScreen(
    applications: List<ApplicationItem>,
    onStatusChange: (appId: String, newStatus: ApplicationStatus, notes: String?) -> Unit,
    onNavigateToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf<ApplicationStatus?>(null) } // null means "All"
    var editingAppItem by remember { mutableStateOf<ApplicationItem?>(null) }
    var editNotesText by remember { mutableStateOf("") }
    var editStatusTarget by remember { mutableStateOf<ApplicationStatus?>(null) }

    val filteredList = remember(applications, selectedTab) {
        if (selectedTab == null) applications else applications.filter { it.status == selectedTab }
    }

    // Counts per status
    val savedCount = applications.count { it.status == ApplicationStatus.SAVED }
    val appliedCount = applications.count { it.status == ApplicationStatus.APPLIED }
    val shortlistedCount = applications.count { it.status == ApplicationStatus.SHORTLISTED }
    val interviewCount = applications.count { it.status == ApplicationStatus.INTERVIEW }
    val selectedCount = applications.count { it.status == ApplicationStatus.SELECTED }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("applications_screen")
    ) {
        // Pipeline Stage Overview Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text(
                    text = "Application Pipeline Tracker",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage your application stages from Saved to Offer Letter",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Status Filter Chips (Kanban columns)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // All Chip
                    FilterChip(
                        selected = selectedTab == null,
                        onClick = { selectedTab = null },
                        label = { Text("All (${applications.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CampusPrimary,
                            selectedLabelColor = Color.White
                        )
                    )

                    ApplicationStatus.entries.forEach { status ->
                        val count = applications.count { it.status == status }
                        val isSelected = selectedTab == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTab = status },
                            label = { Text("${status.title} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CampusPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Applications List
        if (filteredList.isEmpty()) {
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
                        imageVector = Icons.Default.AssignmentTurnedIn,
                        contentDescription = "Empty",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (selectedTab == null) "No tracked applications yet" else "No applications in ${selectedTab?.title}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Discover high-match internships and hackathons in Explore to start tracking.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onNavigateToExplore) {
                        Text("Explore Opportunities")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    ApplicationTrackerCard(
                        application = item,
                        onEditClick = {
                            editingAppItem = item
                            editNotesText = item.notes
                            editStatusTarget = item.status
                        },
                        onQuickAdvance = { nextStatus ->
                            onStatusChange(item.id, nextStatus, item.notes)
                        }
                    )
                }
            }
        }
    }

    // Edit Status & Notes Dialog
    if (editingAppItem != null) {
        val app = editingAppItem!!
        AlertDialog(
            onDismissRequest = { editingAppItem = null },
            title = {
                Text(
                    text = "Update Application Status",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = app.opportunityTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = CampusPrimary
                    )
                    Text(
                        text = app.organization,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Current Stage:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ApplicationStatus.entries.forEach { status ->
                            FilterChip(
                                selected = editStatusTarget == status,
                                onClick = { editStatusTarget = status },
                                label = { Text(status.title) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editNotesText,
                        onValueChange = { editNotesText = it },
                        label = { Text("Recruiter notes / Next steps") },
                        placeholder = { Text("e.g., Coding test completed, interview on Thursday") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newStatus = editStatusTarget ?: app.status
                        onStatusChange(app.id, newStatus, editNotesText)
                        editingAppItem = null
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingAppItem = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ApplicationTrackerCard(
    application: ApplicationItem,
    onEditClick: () -> Unit,
    onQuickAdvance: (ApplicationStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (application.status) {
        ApplicationStatus.SAVED -> CampusPrimary
        ApplicationStatus.APPLIED -> CampusWarning
        ApplicationStatus.SHORTLISTED -> Color(0xFF8B5CF6)
        ApplicationStatus.INTERVIEW -> Color(0xFF0284C7)
        ApplicationStatus.SELECTED -> CampusAccent
        ApplicationStatus.REJECTED -> CampusUrgent
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("application_card_${application.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category & Status Badge & Edit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = application.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = application.status.title.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit status", modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Org
            Text(
                text = application.opportunityTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = application.organization,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Step Bar (Saved -> Applied -> Shortlisted -> Interview -> Selected)
            val stepFraction = when (application.status) {
                ApplicationStatus.SAVED -> 0.2f
                ApplicationStatus.APPLIED -> 0.4f
                ApplicationStatus.SHORTLISTED -> 0.6f
                ApplicationStatus.INTERVIEW -> 0.8f
                ApplicationStatus.SELECTED -> 1.0f
                ApplicationStatus.REJECTED -> 1.0f
            }

            LinearProgressIndicator(
                progress = { stepFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Application Date & Deadline Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Applied/Saved: ${application.applicationDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    text = "Deadline: ${application.deadline}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Notes if any
            if (application.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝 ${application.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Quick Next Step Action Button
            val nextStatus = getNextStatus(application.status)
            if (nextStatus != null) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onQuickAdvance(nextStatus) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Advance to ${nextStatus.title}", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

private fun getNextStatus(current: ApplicationStatus): ApplicationStatus? {
    return when (current) {
        ApplicationStatus.SAVED -> ApplicationStatus.APPLIED
        ApplicationStatus.APPLIED -> ApplicationStatus.SHORTLISTED
        ApplicationStatus.SHORTLISTED -> ApplicationStatus.INTERVIEW
        ApplicationStatus.INTERVIEW -> ApplicationStatus.SELECTED
        else -> null
    }
}
