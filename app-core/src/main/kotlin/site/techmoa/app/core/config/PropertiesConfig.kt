package site.techmoa.app.core.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import site.techmoa.app.core.auth.oidc.OauthProperties

@Configuration
@EnableConfigurationProperties(OauthProperties::class)
class PropertiesConfig {
}