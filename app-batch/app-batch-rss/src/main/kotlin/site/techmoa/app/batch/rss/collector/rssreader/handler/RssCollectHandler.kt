package site.techmoa.app.batch.rss.collector.rssreader.handler

interface RssCollectHandler {
    fun getOrder(): Int
    fun handle(context: RssCollectContext, chain: RssCollectHandlerChain)
}
