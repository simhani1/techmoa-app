package site.techmoa.app.infra.oauth.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import site.techmoa.app.infra.oauth.OauthProperties

@Configuration
@EnableConfigurationProperties(OauthProperties::class)
class PropertiesConfig {
}