package site.techmoa.app.common.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import site.techmoa.app.common.auth.jwt.JwtProperty

@Configuration
@EnableConfigurationProperties(JwtProperty::class)
class JwtConfig {
}