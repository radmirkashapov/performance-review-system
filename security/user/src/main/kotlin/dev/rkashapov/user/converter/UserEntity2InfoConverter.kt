package dev.rkashapov.user.converter

import dev.rkashapov.base.model.UserRole
import dev.rkashapov.user.api.model.UserInfo
import dev.rkashapov.user.entity.UserEntity
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class UserEntity2InfoConverter : Converter<UserEntity, UserInfo> {
    override fun convert(source: UserEntity): UserInfo {
        return UserInfo(
            id = checkNotNull(source.id),
            avatarUrlTemplate = source.avatarUrl,
            realName = source.realName,
            authorities = listOf(source.role).apply {
                if (source.role == UserRole.ADMIN) {
                    this.plus(UserRole.USER) // FIXME reformat
                }
            }
        )
    }
}
