package site.techmoa.app.core.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import site.techmoa.app.core.auth.jwt.JwtProperty

@Configuration
@EnableConfigurationProperties(JwtProperty::class)
class JwtConfig {
}