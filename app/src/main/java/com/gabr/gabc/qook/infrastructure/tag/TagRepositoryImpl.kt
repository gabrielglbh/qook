package com.gabr.gabc.qook.infrastructure.tag

import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.domain.tag.TagRepository

class TagRepositoryImpl : TagRepository {
    override suspend fun createTag(tag: Tag) {
        TODO("Not yet implemented")
    }

    override suspend fun removeTag(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTag(tag: Tag) {
        TODO("Not yet implemented")
    }

    override suspend fun getTags(): List<Tag> {
        TODO("Not yet implemented")
    }

    override suspend fun getTags(recipeId: String): List<Tag> {
        TODO("Not yet implemented")
    }
}