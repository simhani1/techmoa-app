package site.techmoa.app.batch.rss.collector.rssreader.handler.chain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectContext
import site.techmoa.app.batch.rss.collector.rssreader.handler.RssCollectHandlerChain
import site.techmoa.app.storage.db.entity.ArticleEntity
import site.techmoa.app.storage.db.repository.ArticleRepository

class SaveNewArticlesHandlerTest : DescribeSpec({

    lateinit var handler: SaveNewArticlesHandler
    lateinit var articleRepository: ArticleRepository

    beforeTest {
        articleRepository = mockk<ArticleRepository>()
        handler = SaveNewArticlesHandler(articleRepository)
    }

    describe("SaveNewArticlesHandler는") {
        it("4번째로 동작한다") {
            handler.getOrder().shouldBe(4)
        }
        it("newArticles가 비어있으면 저장하지 않는다") {
            val articleRepository = mockk<ArticleRepository>(relaxed = true)
            val context = RssCollectContext(newArticles = emptyList())
            val chain = RssCollectHandlerChain(emptyList())

            handler.handle(context, chain)

            verify(exactly = 0) { articleRepository.saveAll(any<List<ArticleEntity>>()) }
        }
        it("newArticles가 있으면 저장한다") {
            val articleRepository = mockk<ArticleRepository>()
            val handler = SaveNewArticlesHandler(articleRepository)
            val articles = listOf(
                ArticleEntity.of(1L, "t1", "l1", "g1", 1L),
                ArticleEntity.of(1L, "t2", "l2", "g2", 2L),
            )
            val context = RssCollectContext(newArticles = articles)
            val chain = RssCollectHandlerChain(emptyList())
            every { articleRepository.saveAll(articles) } returns articles

            handler.handle(context, chain)

            verify(exactly = 1) { articleRepository.saveAll(articles) }
        }
    }
})