package site.techmoa.app.blog.controller.request

import site.techmoa.app.blog.domain.BlogProfile

data class SubscribeBlogRequest(
    val link: String,
    val name: String,
    val logoUrl: String,
    val rssLink: String
) {
    fun toBlog(): BlogProfile {
        return BlogProfile(
            link = link,
            name = name,
            logoUrl = logoUrl,
            rssLink = rssLink
        )
    }
}