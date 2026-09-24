package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.CampusDataRepository
import com.example.model.ApplicationStatus
import com.example.model.Opportunity
import com.example.model.StudentProfile
import com.example.ui.components.OpportunityDetailSheet
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.ApplicationsScreen
import com.example.ui.screens.DeadlinesScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.CampusAccent
import com.example.ui.theme.CampusPrimary
import com.example.ui.theme.CampusUrgent
import kotlinx.coroutines.launch

enum class AppDestination(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Home", Icons.Default.Home),
    EXPLORE("Explore", Icons.Default.Explore),
    AI_ASSISTANT("Ask AI", Icons.Default.AutoAwesome),
    APPLICATIONS("Tracker", Icons.Default.AssignmentTurnedIn),
    PROFILE("Profile", Icons.Default.Person)
}

enum class OverlayScreen {
    NONE,
    DEADLINES,
    NOTIFICATIONS,
    ONBOARDING,
    ADMIN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusConnectApp() {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Repository state
    val profile by CampusDataRepository.studentProfile.collectAsState()
    val opportunities by CampusDataRepository.opportunities.collectAsState()
    val announcements by CampusDataRepository.announcements.collectAsState()
    val savedIds by CampusDataRepository.savedIds.collectAsState()
    val applications by CampusDataRepository.applications.collectAsState()
    val notifications by CampusDataRepository.notifications.collectAsState()
    val isAdminMode by CampusDataRepository.isAdminMode.collectAsState()

    // Navigation state
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    var currentOverlay by remember { mutableStateOf(OverlayScreen.NONE) }

    // Detail BottomSheet state
    var selectedOpportunity by remember { mutableStateOf<Opportunity?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val unreadNotificationsCount = notifications.count { !it.isRead }
    val urgentDeadlinesCount = opportunities.count { it.daysRemaining <= 3 }
    val activeApplicationsCount = applications.count { it.status != ApplicationStatus.SAVED }

    // Handle Admin overlay toggle
    val effectiveOverlay = when {
        isAdminMode -> OverlayScreen.ADMIN
        else -> currentOverlay
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (effectiveOverlay != OverlayScreen.ONBOARDING) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = CampusPrimary,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Logo",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "CampusConnect",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (effectiveOverlay == OverlayScreen.ADMIN) "Coordinator Mode" else profile.college.substringBefore(","),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (effectiveOverlay == OverlayScreen.ADMIN) CampusPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        if (effectiveOverlay != OverlayScreen.NONE && effectiveOverlay != OverlayScreen.ADMIN) {
                            IconButton(onClick = { currentOverlay = OverlayScreen.NONE }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    actions = {
                        if (effectiveOverlay == OverlayScreen.NONE) {
                            // Deadlines Calendar with badge
                            IconButton(
                                onClick = { currentOverlay = OverlayScreen.DEADLINES },
                                modifier = Modifier.testTag("top_bar_deadlines_btn")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (urgentDeadlinesCount > 0) {
                                            Badge(containerColor = CampusUrgent) {
                                                Text("$urgentDeadlinesCount")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Deadlines",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Notifications with badge
                            IconButton(
                                onClick = { currentOverlay = OverlayScreen.NOTIFICATIONS },
                                modifier = Modifier.testTag("top_bar_notifications_btn")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotificationsCount > 0) {
                                            Badge(containerColor = CampusPrimary) {
                                                Text("$unreadNotificationsCount")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (effectiveOverlay == OverlayScreen.NONE) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    AppDestination.entries.forEach { destination ->
                        val isSelected = currentDestination == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            icon = {
                                if (destination == AppDestination.APPLICATIONS && activeApplicationsCount > 0) {
                                    BadgedBox(badge = { Badge { Text("$activeApplicationsCount") } }) {
                                        Icon(imageVector = destination.icon, contentDescription = destination.title)
                                    }
                                } else {
                                    Icon(imageVector = destination.icon, contentDescription = destination.title)
                                }
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CampusPrimary,
                                selectedTextColor = CampusPrimary,
                                indicatorColor = CampusPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (effectiveOverlay) {
                OverlayScreen.ONBOARDING -> {
                    OnboardingScreen(
                        currentProfile = profile,
                        onComplete = { newProfile ->
                            CampusDataRepository.updateProfile(newProfile)
                            currentOverlay = OverlayScreen.NONE
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Welcome ${newProfile.name}! Your feed has been personalized with Gemini AI.")
                            }
                        },
                        onSkip = { currentOverlay = OverlayScreen.NONE }
                    )
                }

                OverlayScreen.ADMIN -> {
                    AdminScreen(
                        opportunities = opportunities,
                        announcements = announcements,
                        onAddOpportunity = { opp ->
                            CampusDataRepository.addOpportunity(opp)
                            coroutineScope.launch { snackbarHostState.showSnackbar("Opportunity published!") }
                        },
                        onDeleteOpportunity = { id ->
                            CampusDataRepository.deleteOpportunity(id)
                            coroutineScope.launch { snackbarHostState.showSnackbar("Opportunity deleted.") }
                        },
                        onAddAnnouncement = { ann ->
                            CampusDataRepository.addAnnouncement(ann)
                            coroutineScope.launch { snackbarHostState.showSnackbar("Announcement posted!") }
                        },
                        onDeleteAnnouncement = { id ->
                            CampusDataRepository.deleteAnnouncement(id)
                            coroutineScope.launch { snackbarHostState.showSnackbar("Announcement removed.") }
                        },
                        onExitAdmin = {
                            CampusDataRepository.setAdminMode(false)
                            currentOverlay = OverlayScreen.NONE
                        }
                    )
                }

                OverlayScreen.DEADLINES -> {
                    DeadlinesScreen(
                        opportunities = opportunities,
                        savedIds = savedIds,
                        onOpportunityClick = { opp -> selectedOpportunity = opp },
                        onApplyClick = { opp ->
                            CampusDataRepository.applyToOpportunity(opp)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Applied to ${opp.title}! Track progress in Applications.")
                            }
                        }
                    )
                }

                OverlayScreen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onNotificationClick = { notification ->
                            CampusDataRepository.markNotificationAsRead(notification.id)
                            notification.relatedOpportunityId?.let { oppId ->
                                val matched = opportunities.firstOrNull { it.id == oppId }
                                if (matched != null) {
                                    selectedOpportunity = matched
                                }
                            }
                        },
                        onMarkAllRead = {
                            CampusDataRepository.markAllNotificationsAsRead()
                        }
                    )
                }

                OverlayScreen.NONE -> {
                    when (currentDestination) {
                        AppDestination.HOME -> {
                            HomeScreen(
                                studentProfile = profile,
                                opportunities = opportunities,
                                announcements = announcements,
                                savedIds = savedIds,
                                applications = applications,
                                onOpportunityClick = { opp -> selectedOpportunity = opp },
                                onSaveToggle = { opp -> CampusDataRepository.toggleSave(opp) },
                                onNavigateToExplore = { currentDestination = AppDestination.EXPLORE },
                                onNavigateToDeadlines = { currentOverlay = OverlayScreen.DEADLINES },
                                onNavigateToAiAssistant = { currentDestination = AppDestination.AI_ASSISTANT }
                            )
                        }

                        AppDestination.EXPLORE -> {
                            ExploreScreen(
                                opportunities = opportunities,
                                savedIds = savedIds,
                                onOpportunityClick = { opp -> selectedOpportunity = opp },
                                onSaveToggle = { opp -> CampusDataRepository.toggleSave(opp) }
                            )
                        }

                        AppDestination.AI_ASSISTANT -> {
                            AiAssistantScreen(
                                studentProfile = profile,
                                opportunities = opportunities,
                                onOpportunityClick = { opp -> selectedOpportunity = opp }
                            )
                        }

                        AppDestination.APPLICATIONS -> {
                            ApplicationsScreen(
                                applications = applications,
                                onStatusChange = { id, status, notes ->
                                    CampusDataRepository.updateApplicationStatus(id, status, notes)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Application updated to ${status.title}")
                                    }
                                },
                                onNavigateToExplore = { currentDestination = AppDestination.EXPLORE }
                            )
                        }

                        AppDestination.PROFILE -> {
                            ProfileScreen(
                                studentProfile = profile,
                                savedCount = savedIds.size,
                                appliedCount = applications.count { it.status != ApplicationStatus.SAVED },
                                isAdminMode = isAdminMode,
                                onToggleAdminMode = { CampusDataRepository.toggleAdminMode() },
                                onUpdateProfile = { newProfile -> CampusDataRepository.updateProfile(newProfile) },
                                onRestartOnboarding = { currentOverlay = OverlayScreen.ONBOARDING }
                            )
                        }
                    }
                }
            }
        }
    }

    // Opportunity Detail BottomSheet
    if (selectedOpportunity != null) {
        val opp = selectedOpportunity!!
        val isOppSaved = savedIds.contains(opp.id)
        val isOppApplied = applications.any { it.opportunityId == opp.id && it.status != ApplicationStatus.SAVED }

        OpportunityDetailSheet(
            opportunity = opp,
            isSaved = isOppSaved,
            isApplied = isOppApplied,
            studentProfile = profile,
            onDismiss = { selectedOpportunity = null },
            onSaveToggle = { targetOpp -> CampusDataRepository.toggleSave(targetOpp) },
            onApply = { targetOpp ->
                CampusDataRepository.applyToOpportunity(targetOpp)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("🎉 Application submitted! Added to your Applications tracker.")
                }
            },
            sheetState = sheetState
        )
    }
}
