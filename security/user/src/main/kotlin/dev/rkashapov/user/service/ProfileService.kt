package dev.rkashapov.user.service

import dev.rkashapov.security.core.model.CurrentUser
import dev.rkashapov.user.api.model.UserInfo
import dev.rkashapov.user.converter.UserEntity2InfoConverter
import dev.rkashapov.user.repository.UserRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ProfileService(
    private val converter: UserEntity2InfoConverter,
    private val userRepository: UserRepository
) : KLogging() {

    @Throws(NoSuchElementException::class)
    fun me(user: CurrentUser): UserInfo {
        val userEntity = userRepository.findById(user.id).orElseThrow()

        logger.debug { "Found user entity: $userEntity" }

        return converter.convert(userEntity)
    }

}
