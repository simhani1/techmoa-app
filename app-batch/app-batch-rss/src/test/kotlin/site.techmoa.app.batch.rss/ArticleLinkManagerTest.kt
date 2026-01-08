package site.techmoa.app.batch.rss

import io.kotest.matchers.shouldBe
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ArticleLinkManagerTest {

    @InjectMockKs
    private lateinit var articleLinkManager: ArticleLinkManager

    @Test
    fun `아티클 link가 https로 시작하면 그대로 저장한다`() {
        // GIVEN

        val blogLink = "https://blog.com"
        val link = "https://example.com"

        // WHEN
        val result = articleLinkManager.normalize(blogLink, link)

        // THEN
        result.shouldBe(link)
    }

    @Test
    fun `아티클 link가 http로 시작하면 그대로 저장한다`() {
        // GIVEN

        val blogLink = "https://blog.com"
        val link = "http://example.com"

        // WHEN
        val result = articleLinkManager.normalize(blogLink, link)

        // THEN
        result.shouldBe(link)
    }

    @Test
    fun `아티클 link가 https, http로 시작하지 않으면 블로그 링크와 링크를 조합하여 저장한다`() {
        // GIVEN

        val blogLink = "https://blog.com"
        val link = "/post/1"

        // WHEN
        val result = articleLinkManager.normalize(blogLink, link)

        // THEN
        result.shouldBe(blogLink + link)
    }
}