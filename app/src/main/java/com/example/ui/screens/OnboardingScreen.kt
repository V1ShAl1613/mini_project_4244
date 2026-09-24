package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StudentProfile
import com.example.ui.theme.CampusAccent
import com.example.ui.theme.CampusPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    currentProfile: StudentProfile,
    onComplete: (StudentProfile) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) } // 1..4

    // Working state
    var name by remember { mutableStateOf(currentProfile.name) }
    var college by remember { mutableStateOf(currentProfile.college) }
    var degree by remember { mutableStateOf(currentProfile.degree) }
    var dept by remember { mutableStateOf(currentProfile.department) }
    var year by remember { mutableStateOf(currentProfile.academicYear) }
    var cgpa by remember { mutableStateOf(currentProfile.cgpa) }

    var selectedSkills by remember { mutableStateOf(currentProfile.skills.toSet()) }
    var selectedInterests by remember { mutableStateOf(currentProfile.interests.toSet()) }
    var selectedGoals by remember { mutableStateOf(currentProfile.careerGoals.toSet()) }

    val popularSkills = listOf(
        "Python", "Java", "C++", "React", "Node.js", "SQL", "Machine Learning",
        "NLP", "Data Structures", "Docker", "Git", "Kotlin", "Android", "UI/UX Design",
        "FastAPI", "MongoDB", "Spring Boot", "Cybersecurity", "Flutter", "Cloud (AWS/GCP)"
    )

    val popularInterests = listOf(
        "Artificial Intelligence", "Full Stack Web Development", "Mobile Applications",
        "Competitive Programming", "Cybersecurity", "Cloud Architecture", "Data Science & Analytics",
        "Robotics & IoT", "Open Source Software", "Blockchain & Web3", "Fintech"
    )

    val popularGoals = listOf(
        "Land Tier-1 Product MNC (Google, Microsoft, Amazon)",
        "Join High-Growth Tech Startup",
        "Win National Hackathons & Competitions",
        "Publish Research in AI/ML",
        "Pursue MS / Higher Education Abroad",
        "Secure Paid Remote Internships",
        "Found a Tech Startup"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("onboarding_screen")
    ) {
        // Top Step Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    IconButton(onClick = { step -= 1 }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Text(
                    text = "Step $step of 4",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = CampusPrimary
                )

                TextButton(onClick = onSkip) {
                    Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { step / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = CampusPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // Step Content with Vertical Scroll
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            when (step) {
                1 -> {
                    // Step 1: Academic Profile
                    Text(
                        text = "Let's personalize your campus opportunities",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tell us about your college and current academic program.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = college,
                        onValueChange = { college = it },
                        label = { Text("College / University") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = degree,
                        onValueChange = { degree = it },
                        label = { Text("Degree (e.g. B.Tech, BCA, MCA)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = dept,
                        onValueChange = { dept = it },
                        label = { Text("Department / Branch (e.g. CSE, IT, ECE)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = year,
                            onValueChange = { year = it },
                            label = { Text("Year (e.g. 3rd Year)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = cgpa,
                            onValueChange = { cgpa = it },
                            label = { Text("CGPA (e.g. 8.9)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                2 -> {
                    // Step 2: Technical & Soft Skills
                    Text(
                        text = "What skills do you possess?",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Select all languages, tools, and frameworks you know. Gemini uses this to match jobs and internships.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        popularSkills.forEach { skill ->
                            val isSelected = selectedSkills.contains(skill)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedSkills = if (isSelected) selectedSkills - skill else selectedSkills + skill
                                },
                                label = { Text(skill) },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CampusPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                3 -> {
                    // Step 3: Areas of Interest
                    Text(
                        text = "What areas are you passionate about?",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Select domains that excite you so we can curate hackathons, competitions, and workshops.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        popularInterests.forEach { interest ->
                            val isSelected = selectedInterests.contains(interest)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedInterests = if (isSelected) selectedInterests - interest else selectedInterests + interest
                                },
                                label = { Text(interest) },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CampusPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                4 -> {
                    // Step 4: Career Goals
                    Text(
                        text = "What are your immediate career goals?",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Help CampusConnect prioritize high-impact opportunities for you.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        popularGoals.forEach { goal ->
                            val isSelected = selectedGoals.contains(goal)
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) CampusPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedGoals = if (isSelected) selectedGoals - goal else selectedGoals + goal
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = if (isSelected) CampusPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = CircleShape,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        if (isSelected) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = goal,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) CampusPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Bottom Navigation Action
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (step < 4) {
                            step += 1
                        } else {
                            val updated = currentProfile.copy(
                                name = name.ifBlank { currentProfile.name },
                                college = college.ifBlank { currentProfile.college },
                                degree = degree.ifBlank { currentProfile.degree },
                                department = dept.ifBlank { currentProfile.department },
                                academicYear = year.ifBlank { currentProfile.academicYear },
                                cgpa = cgpa.ifBlank { currentProfile.cgpa },
                                skills = selectedSkills.toList().ifEmpty { currentProfile.skills },
                                interests = selectedInterests.toList().ifEmpty { currentProfile.interests },
                                careerGoals = selectedGoals.toList().ifEmpty { currentProfile.careerGoals }
                            )
                            onComplete(updated)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CampusPrimary)
                ) {
                    Text(if (step < 4) "Continue" else "Finish & Personalize My Feed")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
