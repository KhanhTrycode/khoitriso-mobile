package com.example.khoitriso.utils

object YouTubeUtils {
    /**
     * Extract YouTube video ID from various YouTube URL formats
     */
    fun extractVideoId(url: String): String? {
        val patterns = listOf(
            "(?:youtube\\.com\\/watch\\?v=|youtu\\.be\\/|youtube\\.com\\/embed\\/)([^&\\n?#]+)",
            "youtube\\.com\\/watch\\?.*v=([^&\\n?#]+)",
            "youtu\\.be\\/([^&\\n?#]+)"
        )
        
        for (pattern in patterns) {
            val regex = Regex(pattern)
            val match = regex.find(url)
            if (match != null) {
                return match.groupValues[1]
            }
        }
        return null
    }
    
    /**
     * Check if URL is a YouTube URL
     */
    fun isYouTubeUrl(url: String): Boolean {
        return url.contains("youtube.com") || url.contains("youtu.be")
    }
    
    /**
     * Convert YouTube URL to embed URL
     */
    fun getEmbedUrl(url: String): String? {
        val videoId = extractVideoId(url)
        return videoId?.let { "https://www.youtube.com/embed/$it" }
    }
}

