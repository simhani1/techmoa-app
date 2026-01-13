package site.techmoa.app.batch.rss

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import site.techmoa.app.core.article.ArticlePort
import site.techmoa.app.core.blog.BlogPort
import site.techmoa.app.core.blog.BlogStatus
import site.techmoa.app.core.comb.BlogAndArticleFixture

@ExtendWith(MockKExtension::class)
class RssReaderCollectorTest {

    @MockK
    private lateinit var rssClient: RssClient

    @MockK
    private lateinit var articlePort: ArticlePort

    @MockK
    private lateinit var blogPort: BlogPort

    @InjectMockKs
    private lateinit var collector: RssReaderCollector

    @Test
    fun `guid를 기준으로 새로운 아티클만 저장한다`() {
        // GIVEN
        val map = BlogAndArticleFixture.giveMe(articleCnt = 2, blogCnt = 2)
        val blog1 = map.keys.first { it.id == 1L }
        val blog2 = map.keys.first { it.id == 2L }
        val article1 = map.getValue(blog1)[0]
        val article2 = map.getValue(blog1)[1]
        val article3 = map.getValue(blog2)[0]

        every { blogPort.findAllBy(BlogStatus.ACTIVE) } returns listOf(blog1, blog2)
        every { rssClient.fetch(blog1) } returns listOf(article1, article2)
        every { rssClient.fetch(blog2) } returns listOf(article3)
        every { articlePort.existsByBlogIdAndGuid(article1.blogId, article1.guid) } returns false
        every { articlePort.existsByBlogIdAndGuid(article2.blogId, article2.guid) } returns true
        every { articlePort.existsByBlogIdAndGuid(article3.blogId, article3.guid) } returns false
        every { articlePort.saveAll(any()) } just Runs

        // WHEN
        collector.execute()

        // THEN
        verify(exactly = 1) { articlePort.saveAll(listOf(article1, article3)) }
    }
}
