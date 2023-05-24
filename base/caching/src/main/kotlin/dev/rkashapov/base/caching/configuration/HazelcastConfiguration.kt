package dev.rkashapov.base.caching.configuration

import com.hazelcast.config.Config
import com.hazelcast.config.InMemoryFormat
import com.hazelcast.config.MapConfig
import com.hazelcast.config.cp.CPSubsystemConfig
import com.hazelcast.config.cp.FencedLockConfig
import dev.rkashapov.base.caching.CollectionName
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HazelcastConfiguration {

    @Bean
    fun hazelcastConfig() =
        Config().apply {
            addMapConfig(
                MapConfig(CollectionName.oauthStatesMap)
                    .setInMemoryFormat(InMemoryFormat.OBJECT)
                    .setTimeToLiveSeconds(15 * 60)
            )
            addMapConfig(
                MapConfig(CollectionName.prsTestStatesMap)
                    .setInMemoryFormat(InMemoryFormat.OBJECT)
                    .setTimeToLiveSeconds(60 * 60 * 60 * 3)
            )
            cpSubsystemConfig = CPSubsystemConfig().addLockConfig(FencedLockConfig("prs-lock"))
        }

}
