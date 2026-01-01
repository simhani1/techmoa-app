package site.techmoa.app.batch.rss.collector.rssreader.handler

interface RssCollectHandler {
    fun handle(context: RssCollectContext, chain: RssCollectHandlerChain)
    fun getOrder(): Int
}
