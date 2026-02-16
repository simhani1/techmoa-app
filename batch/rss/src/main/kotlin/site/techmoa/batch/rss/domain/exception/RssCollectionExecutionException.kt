package site.techmoa.batch.rss.domain.exception

class RssCollectionExecutionException(
    message: String,
    cause: Throwable
) : RuntimeException(message, cause)
