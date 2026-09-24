package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.CollegeAnnouncement
import com.example.model.Opportunity
import com.example.model.OpportunityCategory
import com.example.ui.theme.CampusAccent
import com.example.ui.theme.CampusPrimary
import com.example.ui.theme.CampusUrgent
import com.example.ui.theme.CampusWarning
import java.util.UUID

@Composable
fun AdminScreen(
    opportunities: List<Opportunity>,
    announcements: List<CollegeAnnouncement>,
    onAddOpportunity: (Opportunity) -> Unit,
    onDeleteOpportunity: (String) -> Unit,
    onAddAnnouncement: (CollegeAnnouncement) -> Unit,
    onDeleteAnnouncement: (String) -> Unit,
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddOppDialog by remember { mutableStateOf(false) }
    var showAddAnnounceDialog by remember { mutableStateOf(false) }

    // Add Opportunity form state
    var oppTitle by remember { mutableStateOf("") }
    var oppOrg by remember { mutableStateOf("") }
    var oppCategory by remember { mutableStateOf(OpportunityCategory.INTERNSHIP) }
    var oppStipend by remember { mutableStateOf("₹25,000 / month") }
    var oppLocation by remember { mutableStateOf("Bengaluru / Remote") }
    var oppDeadline by remember { mutableStateOf("15 Oct 2026") }
    var oppSkills by remember { mutableStateOf("Python, React, SQL") }
    var oppDescription by remember { mutableStateOf("Exciting opening for college developers.") }

    // Add Announcement form state
    var annTitle by remember { mutableStateOf("") }
    var annDept by remember { mutableStateOf("Training & Placement Cell") }
    var annContent by remember { mutableStateOf("") }
    var annIsUrgent by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = CampusPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Campus Coordinator Portal",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        OutlinedButton(onClick = onExitAdmin) {
                            Text("Exit Admin", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Metrics Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminMetricTile("Students", "2,840", Icons.Default.Group, CampusPrimary, Modifier.weight(1f))
                        AdminMetricTile("Opportunities", "${opportunities.size}", Icons.Default.Work, CampusAccent, Modifier.weight(1f))
                        AdminMetricTile("Notices", "${announcements.size}", Icons.Default.Campaign, CampusWarning, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showAddOppDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Opportunity", style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = { showAddAnnounceDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CampusAccent)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Notice", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Section: Manage Opportunities
        item {
            Text(
                text = "Active Opportunities (${opportunities.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(opportunities.take(10), key = { it.id }) { opp ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "[${opp.category.displayName}] ${opp.title}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${opp.organization} • Deadline: ${opp.deadline}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { onDeleteOpportunity(opp.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CampusUrgent)
                    }
                }
            }
        }

        // Section: Manage Announcements
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "College Announcements (${announcements.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(announcements, key = { it.id }) { ann ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ann.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${ann.department} • ${ann.date}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { onDeleteAnnouncement(ann.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CampusUrgent)
                    }
                }
            }
        }
    }

    // Dialog: Add Opportunity
    if (showAddOppDialog) {
        AlertDialog(
            onDismissRequest = { showAddOppDialog = false },
            title = { Text("Publish New Opportunity", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = oppTitle,
                        onValueChange = { oppTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = oppOrg,
                        onValueChange = { oppOrg = it },
                        label = { Text("Organization / Company") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    // Category Chips
                    Text("Category:", style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OpportunityCategory.entries.filterNot { it == OpportunityCategory.ALL }.forEach { cat ->
                            FilterChip(
                                selected = oppCategory == cat,
                                onClick = { oppCategory = cat },
                                label = { Text(cat.displayName, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = oppStipend,
                        onValueChange = { oppStipend = it },
                        label = { Text("Stipend / Prize") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = oppLocation,
                        onValueChange = { oppLocation = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = oppDeadline,
                        onValueChange = { oppDeadline = it },
                        label = { Text("Deadline (e.g. 15 Oct 2026)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = oppSkills,
                        onValueChange = { oppSkills = it },
                        label = { Text("Required Skills (comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (oppTitle.isNotBlank() && oppOrg.isNotBlank()) {
                            onAddOpportunity(
                                Opportunity(
                                    id = "custom_${UUID.randomUUID()}",
                                    title = oppTitle,
                                    organization = oppOrg,
                                    category = oppCategory,
                                    description = oppDescription,
                                    requiredSkills = oppSkills.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                    eligibility = "Open to undergraduate college students",
                                    location = oppLocation,
                                    isRemote = oppLocation.contains("remote", ignoreCase = true),
                                    stipendOrSalary = oppStipend,
                                    isPaid = true,
                                    deadline = oppDeadline,
                                    daysRemaining = 14,
                                    postedDate = "Just now",
                                    applicationLink = "https://campusconnect.edu/apply"
                                )
                            )
                            oppTitle = ""
                            oppOrg = ""
                            showAddOppDialog = false
                        }
                    }
                ) {
                    Text("Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddOppDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Add Announcement
    if (showAddAnnounceDialog) {
        AlertDialog(
            onDismissRequest = { showAddAnnounceDialog = false },
            title = { Text("Publish College Announcement", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = annTitle,
                        onValueChange = { annTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = annDept,
                        onValueChange = { annDept = it },
                        label = { Text("Department / Authority") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = annContent,
                        onValueChange = { annContent = it },
                        label = { Text("Announcement text") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mark as Action Required / Urgent")
                        Switch(checked = annIsUrgent, onCheckedChange = { annIsUrgent = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (annTitle.isNotBlank() && annContent.isNotBlank()) {
                            onAddAnnouncement(
                                CollegeAnnouncement(
                                    id = "ann_${UUID.randomUUID()}",
                                    title = annTitle,
                                    department = annDept,
                                    date = "Today",
                                    category = "Academic",
                                    content = annContent,
                                    isUrgent = annIsUrgent
                                )
                            )
                            annTitle = ""
                            annContent = ""
                            showAddAnnounceDialog = false
                        }
                    }
                ) {
                    Text("Post Notice")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAnnounceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminMetricTile(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
