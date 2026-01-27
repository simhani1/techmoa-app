package site.techmoa.app.core.blog

class BlogFixture {

    companion object {
        @JvmStatic
        fun giveMeOne(idx: Int): Blog {
            return Blog(
                id = idx.toLong(),
                link = "https://blog$idx.com",
                name = "blog$idx",
                logoUrl = "https://blog$idx.com/logo.png",
                rssLink = "https://blog$idx.com/rss"
            )
        }
    }
}
