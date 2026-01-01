package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import com.apptasticsoftware.rssreader.DateTime
import com.apptasticsoftware.rssreader.Item
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain
import site.techmoa.app.batch.rss.fixture.HandlerFixture
import site.techmoa.app.storage.db.entity.BlogEntity
import site.techmoa.app.storage.db.entity.BlogStatus
import site.techmoa.app.storage.db.repository.ArticleRepository
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

class FilterNewArticlesHandlerTest : DescribeSpec({

    lateinit var handler: FilterNewArticlesHandler
    lateinit var articleRepository: ArticleRepository

    beforeTest {
        articleRepository = mockk<ArticleRepository>()
        handler = FilterNewArticlesHandler(articleRepository)
    }

    describe("FilterNewArticlesHandler는") {
        it("3번째로 동작한다") {
            handler.getOrder().shouldBe(3)
        }
        it("신규 아티클을 모아 context에 저장하고 다음 핸들러를 호출한다") {
            val blog = BlogEntity(1L, "link", "name", "logo", "rss", BlogStatus.ACTIVE)
            Item(DateTime())
            val item = mockk<Item>()
            every { item.title } returns Optional.of("title")
            every { item.link } returns Optional.of("link")
            every { item.guid } returns Optional.of("guid-1")
            every { item.pubDateAsZonedDateTime } returns Optional.of(
                ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
            )
            every { articleRepository.existsByBlogIdAndGuid(1L, "guid-1") } returns false
            val context = RssCollectContext(
                collectedItems = mapOf(blog to listOf(item))
            )

            val nextHandler = spyk(HandlerFixture.giveMeHandler())
            val chain = RssCollectHandlerChain(listOf(nextHandler))

            handler.handle(context, chain)

            context.newArticles.shouldHaveSize(1)
            context.newArticles.first().blogId.shouldBe(1L)
            verify(exactly = 1) { nextHandler.handle(any<RssCollectContext>(), any<RssCollectHandlerChain>()) }
        }
    }
})
