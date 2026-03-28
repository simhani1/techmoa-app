package site.techmoa.worker.rss.domain.exception

class RssCollectionExecutionException(
    message: String,
    cause: Throwable
) : RuntimeException(message, cause)
