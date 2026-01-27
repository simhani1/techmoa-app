package site.techmoa.app.core.auth.dto

class OIDCPubKeyDto(
    val kid: String,
    val alg: String,
    val use: String,
    val n: String,
    val e: String
)