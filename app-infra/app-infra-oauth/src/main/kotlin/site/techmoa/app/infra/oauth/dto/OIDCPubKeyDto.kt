package site.techmoa.app.infra.oauth.dto

class OIDCPubKeyDto(
    val kid: String,
    val alg: String,
    val use: String,
    val n: String,
    val e: String
)