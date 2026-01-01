package site.techmoa.app.batch.rss.collector.rssreader.handler

import com.apptasticsoftware.rssreader.Item
import site.techmoa.app.storage.db.entity.BlogEntity

data class RssCollectContext(
    var blogs: List<BlogEntity> = emptyList(),
    var collectedItems: Map<BlogEntity, List<Item>> = emptyMap(),
)
