package site.techmoa.app.batch.rss.collector.rssreader.handler

class RssCollectHandlerChain(
    private val handlers: List<RssCollectHandler>,
    private val index: Int = 0,
) {
    fun next(context: RssCollectContext) {
        if (index >= handlers.size) {
            return
        }
        val nextChain = RssCollectHandlerChain(handlers, index + 1)
        handlers[index].handle(context, nextChain)
    }
}
