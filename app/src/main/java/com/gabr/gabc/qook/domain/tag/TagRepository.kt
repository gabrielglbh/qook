package com.gabr.gabc.qook.domain.tag

import arrow.core.Either

interface TagRepository {
    suspend fun createTag(tag: Tag): Either<TagFailure, Tag>
    suspend fun removeTag(id: String): Either<TagFailure, Unit>
    suspend fun updateTag(tag: Tag): Either<TagFailure, Unit>
    suspend fun getTags(): Either<TagFailure, List<Tag>>
    suspend fun getTags(recipeId: String): Either<TagFailure, List<Tag>>
}