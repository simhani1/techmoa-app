package site.techmoa.app.batch.rss.collector.rssreader.handler

abstract class AbstractRssCollectHandler : RssCollectHandler {
    private var next: RssCollectHandler? = null

    abstract fun getOrder(): Int

    override fun setNext(next: RssCollectHandler): RssCollectHandler {
        this.next = next
        return next
    }

    protected fun handleNext(context: RssCollectContext) {
        next?.handle(context)
    }
}
