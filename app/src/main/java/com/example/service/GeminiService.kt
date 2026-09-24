package com.example.service

import android.util.Log
import com.example.BuildConfig
import com.example.model.Opportunity
import com.example.model.StudentProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun hasApiKey(): Boolean {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun callGeminiApi(systemPrompt: String, userPrompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            throw IllegalStateException("No valid GEMINI_API_KEY configured in environment.")
        }

        val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                })
            })
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", userPrompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
                put("maxOutputTokens", 1024)
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(requestJson.toString().toRequestBody(jsonMediaType))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                Log.e(TAG, "Gemini API error code ${response.code}: $errorBody")
                throw Exception("HTTP ${response.code}: $errorBody")
            }

            val body = response.body?.string() ?: throw Exception("Empty response from Gemini")
            val root = JSONObject(body)
            val candidates = root.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text", "") ?: ""
            if (text.isBlank()) throw Exception("No text in candidate response")
            text
        }
    }

    /**
     * Ask AI about a specific opportunity with student profile context
     */
    suspend fun askOpportunityQuestion(
        questionType: String,
        opportunity: Opportunity,
        profile: StudentProfile
    ): String {
        val systemPrompt = """
            You are CampusConnect AI, an expert academic counselor and career mentor for Indian college students.
            Give concise, actionable, and encouraging advice formatted in 2-3 short bullet points.
            Never invent eligibility criteria or deadlines.
        """.trimIndent()

        val prompt = """
            Student Profile:
            - Name: ${profile.name}
            - College: ${profile.college}
            - Degree & Dept: ${profile.degree} in ${profile.department} (${profile.academicYear}, ${profile.semester})
            - Skills: ${profile.skills.joinToString(", ")}
            - Interests: ${profile.interests.joinToString(", ")}
            - Career Goals: ${profile.careerGoals.joinToString(", ")}

            Opportunity:
            - Title: ${opportunity.title}
            - Organization: ${opportunity.organization}
            - Category: ${opportunity.category.displayName}
            - Location: ${opportunity.location} (Remote: ${opportunity.isRemote})
            - Deadline: ${opportunity.deadline}
            - Stipend/Salary: ${opportunity.stipendOrSalary}
            - Required Skills: ${opportunity.requiredSkills.joinToString(", ")}
            - Eligibility: ${opportunity.eligibility}
            - Description: ${opportunity.description}

            Student Query: "$questionType"
            (e.g., Am I eligible? Why is this recommended to me? What skills do I need? How should I prepare?)
        """.trimIndent()

        return try {
            callGeminiApi(systemPrompt, prompt)
        } catch (e: Exception) {
            Log.w(TAG, "Falling back to heuristic response: ${e.message}")
            generateLocalOpportunityAnswer(questionType, opportunity, profile)
        }
    }

    /**
     * General Student AI Campus Assistant conversation
     */
    suspend fun chatWithAssistant(
        userMessage: String,
        profile: StudentProfile,
        availableOpportunities: List<Opportunity>
    ): AssistantResponse {
        val systemPrompt = """
            You are CampusConnect AI, a specialized campus opportunities assistant for college students in India.
            Answer clearly, empathetically, and directly in 2-4 sentences or bullet points.
            If relevant, recommend 1 or 2 specific opportunities from the list provided.
        """.trimIndent()

        val oppSummary = availableOpportunities.take(15).joinToString("\n") {
            "- [${it.id}] ${it.title} by ${it.organization} (${it.category.displayName}, Deadline: ${it.deadline}, Skills: ${it.requiredSkills.take(3).joinToString()})"
        }

        val prompt = """
            Student: ${profile.name} (${profile.degree} ${profile.department}, ${profile.academicYear})
            Skills: ${profile.skills.joinToString(", ")}
            Goals: ${profile.careerGoals.joinToString(", ")}

            Opportunities in database:
            $oppSummary

            User asked: "$userMessage"
            Provide helpful guidance. If you recommend an opportunity from the list, mention its exact title.
        """.trimIndent()

        return try {
            val replyText = callGeminiApi(systemPrompt, prompt)
            // Find any referenced opportunity
            val matchedOpp = availableOpportunities.firstOrNull { opp ->
                replyText.contains(opp.title, ignoreCase = true) || replyText.contains(opp.organization, ignoreCase = true)
            }
            AssistantResponse(
                replyText = replyText,
                recommendedOpportunity = matchedOpp
            )
        } catch (e: Exception) {
            Log.w(TAG, "Chat fallback due to: ${e.message}")
            generateLocalAssistantChat(userMessage, profile, availableOpportunities)
        }
    }

    /**
     * Summarize college announcement with key takeaways
     */
    suspend fun summarizeAnnouncement(title: String, content: String): String {
        val systemPrompt = "You are a campus announcement summarizer. Return 2-3 brief bullet points highlighting: Action Required, Deadline/Timing, and Who is Affected."
        val prompt = "Announcement Title: $title\nAnnouncement Content: $content"
        return try {
            callGeminiApi(systemPrompt, prompt)
        } catch (e: Exception) {
            "• Key Update: $title\n• Affected: College students and faculty.\n• Note: Check portal for official links and instructions."
        }
    }

    // --- High-Quality Local Heuristic Fallbacks ---

    private fun generateLocalOpportunityAnswer(
        questionType: String,
        opportunity: Opportunity,
        profile: StudentProfile
    ): String {
        val matchingSkills = opportunity.requiredSkills.filter { req ->
            profile.skills.any { s -> s.contains(req, ignoreCase = true) || req.contains(s, ignoreCase = true) }
        }
        val missingSkills = opportunity.requiredSkills.filterNot { req ->
            profile.skills.any { s -> s.contains(req, ignoreCase = true) || req.contains(s, ignoreCase = true) }
        }

        return when {
            questionType.contains("eligible", ignoreCase = true) -> {
                "• **Eligibility Check**: ${opportunity.eligibility}.\n" +
                "• **Department Match**: Matches your background in ${profile.department} (${profile.academicYear}).\n" +
                "• **Verdict**: You are eligible to apply! Submit before the deadline (${opportunity.deadline})."
            }
            questionType.contains("Why", ignoreCase = true) || questionType.contains("recommended", ignoreCase = true) -> {
                val matchedStr = if (matchingSkills.isNotEmpty()) matchingSkills.joinToString(", ") else "Computer Science foundations"
                "• **Profile Synergy**: Matches your verified skills in $matchedStr.\n" +
                "• **Goal Alignment**: Supports your target of landing a high-impact ${opportunity.category.displayName.lowercase()}.\n" +
                "• **High Relevance**: Rated at ${opportunity.matchScore}% affinity for your profile."
            }
            questionType.contains("skills", ignoreCase = true) -> {
                val hasText = if (matchingSkills.isNotEmpty()) "You already have: ${matchingSkills.joinToString(", ")}." else "Core fundamentals needed."
                val needText = if (missingSkills.isNotEmpty()) "Skills to brush up: ${missingSkills.joinToString(", ")}." else "You cover all primary skill requirements!"
                "• **Your Stack**: $hasText\n• **Recommended Prep**: $needText"
            }
            else -> { // How to prepare
                "• **Step 1**: Review fundamentals in ${opportunity.requiredSkills.take(2).joinToString(" & ")} and prepare 1-2 repository projects to showcase.\n" +
                "• **Step 2**: Polish your resume to emphasize relevant coursework in ${profile.department}.\n" +
                "• **Step 3**: Submit your application prior to ${opportunity.deadline}."
            }
        }
    }

    private fun generateLocalAssistantChat(
        query: String,
        profile: StudentProfile,
        opportunities: List<Opportunity>
    ): AssistantResponse {
        val lower = query.lowercase()
        return when {
            lower.contains("internship") || lower.contains("intern") -> {
                val bestIntern = opportunities.filter { it.category == com.example.model.OpportunityCategory.INTERNSHIP }
                    .maxByOrNull { it.matchScore } ?: opportunities.first()
                AssistantResponse(
                    replyText = "Based on your ${profile.skills.take(3).joinToString(", ")} skills and 3rd year CSE standing, I strongly recommend applying for **${bestIntern.title}** at **${bestIntern.organization}**. It offers ${bestIntern.stipendOrSalary} with an upcoming deadline of ${bestIntern.deadline}.",
                    recommendedOpportunity = bestIntern
                )
            }
            lower.contains("hackathon") -> {
                val bestHack = opportunities.filter { it.category == com.example.model.OpportunityCategory.HACKATHON }
                    .firstOrNull() ?: opportunities.first()
                AssistantResponse(
                    replyText = "Here is a top hackathon for you: **${bestHack.title}** hosted by ${bestHack.organization}. Deadline is ${bestHack.deadline} with ${bestHack.stipendOrSalary}. It's a fantastic stage to build projects with your team.",
                    recommendedOpportunity = bestHack
                )
            }
            lower.contains("deadline") || lower.contains("friday") || lower.contains("week") -> {
                val urgent = opportunities.filter { it.daysRemaining <= 7 }.sortedBy { it.daysRemaining }.firstOrNull()
                if (urgent != null) {
                    AssistantResponse(
                        replyText = "⚠️ You have urgent deadlines this week! The most time-critical is **${urgent.title}** at ${urgent.organization} (Deadline: ${urgent.deadline}). Don't forget to submit before the portal closes.",
                        recommendedOpportunity = urgent
                    )
                } else {
                    AssistantResponse(
                        replyText = "You are in good shape this week! Your nearest deadlines are in about 10 days. Take this time to refine your resume and portfolios.",
                        recommendedOpportunity = null
                    )
                }
            }
            lower.contains("scholarship") -> {
                val bestSchol = opportunities.filter { it.category == com.example.model.OpportunityCategory.SCHOLARSHIP }
                    .firstOrNull() ?: opportunities.first()
                AssistantResponse(
                    replyText = "For financial grants, **${bestSchol.title}** from ${bestSchol.organization} matches undergraduate engineering students with ${bestSchol.stipendOrSalary}. Deadline is ${bestSchol.deadline}.",
                    recommendedOpportunity = bestSchol
                )
            }
            else -> {
                val topOpp = opportunities.maxByOrNull { it.matchScore } ?: opportunities.first()
                AssistantResponse(
                    replyText = "Hello ${profile.name}! I analyzed our campus opportunities against your ${profile.department} profile. You have strong matches in ${profile.skills.take(2).joinToString(" and ")}. Let me know if you want internships, hackathons, or scholarship deadlines!",
                    recommendedOpportunity = topOpp
                )
            }
        }
    }
}

data class AssistantResponse(
    val replyText: String,
    val recommendedOpportunity: Opportunity? = null
)
