package com.gabr.gabc.qook.domain.tag

interface TagRepository {
    suspend fun createTag(tag: Tag)
    suspend fun removeTag(id: String)
    suspend fun updateTag(tag: Tag)
    suspend fun getTags(): List<Tag>
    suspend fun getTags(recipeId: String): List<Tag>
}