package site.techmoa.infrastructure.oauth.support.oidc

class OIDCPubKeyDto(
    val kid: String,
    val alg: String,
    val use: String,
    val n: String,
    val e: String
)