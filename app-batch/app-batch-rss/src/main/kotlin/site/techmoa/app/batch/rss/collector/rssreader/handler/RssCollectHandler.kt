package site.techmoa.app.batch.rss.collector.rssreader.handler

interface RssCollectHandler {
    fun setNext(next: RssCollectHandler): RssCollectHandler
    fun handle(context: RssCollectContext)
}
