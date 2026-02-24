package site.techmoa.batch.rss.support

import com.apptasticsoftware.rssreader.Item
import com.apptasticsoftware.rssreader.RssReader
import org.springframework.stereotype.Component
import site.techmoa.batch.rss.domain.model.Article
import site.techmoa.batch.rss.domain.model.Blog
import java.time.ZonedDateTime

/**
 * 외부 라이브러리를 사용하여 XML을 객체로 변환합니다.
 *
 * NOTE:
 * - 현재 RSS 파싱 과정에서 발생하는 예외 처리를 위해
 *   임시로 커스텀 필터 클래스를 사용하고 있습니다.
 * - 해당 필터는 추후 라이브러리 교체 또는
 *   파싱 전략 변경 시 제거 예정입니다.
 *
 * @see com.apptasticsoftware.rssreader.RssReader
 * @see CustomInvalidXmlCharacterFilter
 */
@Component
class RssReaderClient(
    private val articleLinkManager: ArticleLinkManager,
) : RssClient {

    override fun fetch(blog: Blog): List<Article> {
        val rssReader = RssReader().addFeedFilter(CustomInvalidXmlCharacterFilter()) as RssReader
        val rawItems = rssReader.read(blog.rssLink)
            .toList()
        val items = rawItems.map { toItem(it, blog) }
            .filterIsInstance<ParsedItem.Valid>()
        return items.map {
            Article.of(
                blogId = blog.id,
                title = it.title,
                link = it.link,
                guid = it.guid,
                pubDate = it.pubDate
            )
        }
    }

    private fun toItem(rssItem: Item, blog: Blog): ParsedItem {
        val title = rssItem.title.orElse(nothing()) ?: return ParsedItem.Invalid
        val link = rssItem.link.orElse(nothing()) ?: return ParsedItem.Invalid
        val guid = rssItem.guid.orElse(nothing()) ?: return ParsedItem.Invalid
        val pubZonedDateTime = rssItem.pubDateAsZonedDateTime.orElse(nothing()) ?: return ParsedItem.Invalid

        val sanitizedLink = sanitizeLink(blog.link, link)
        val pubDate = toEpochMillis(pubZonedDateTime)

        return ParsedItem.Valid(
            title = title,
            link = sanitizedLink,
            guid = guid,
            pubDate = pubDate
        )
    }

    private fun sanitizeLink(blogLink: String, articleLink: String): String {
        return articleLinkManager.sanitizeLink(blogLink, articleLink)
    }

    private fun toEpochMillis(pubZonedDateTime: ZonedDateTime): Long {
        return pubZonedDateTime.toInstant().toEpochMilli()
    }

    private fun nothing() : Nothing? {
        return null
    }
}
