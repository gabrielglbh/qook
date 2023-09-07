package com.gabr.gabc.qook.domain.tag

import arrow.core.Either
import com.gabr.gabc.qook.infrastructure.recipe.RecipeDto

interface TagRepository {
    suspend fun createTag(tag: Tag): Either<TagFailure, Tag>
    suspend fun removeTag(id: String): Either<TagFailure, Unit>
    suspend fun updateTag(tag: Tag): Either<TagFailure, Unit>
    suspend fun getTags(): Either<TagFailure, List<Tag>>
    suspend fun getTags(recipeDto: RecipeDto, userId: String): Either<TagFailure, List<Tag>>
}