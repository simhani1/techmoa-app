//package site.techmoa.app.article.service
//
//import io.kotest.matchers.booleans.shouldBeFalse
//import io.kotest.matchers.booleans.shouldBeTrue
//import io.kotest.matchers.ints.shouldBeZero
//import io.kotest.matchers.nulls.shouldBeNull
//import io.kotest.matchers.nulls.shouldNotBeNull
//import io.kotest.matchers.shouldBe
//import io.mockk.every
//import org.junit.jupiter.api.Test
//import site.techmoa.app.core.OffsetLimit
//import site.techmoa.app.fixture.article.ArticleFixture
//import site.techmoa.app.fixture.blog.BlogFixture
//
//class ArticleServiceTest: ArticleServiceUnitTest() {
//
//    @Test
//    fun `요청한 개수보다 한 개 더 많은 데이터가 반환되면 다음 페이지는 존재한다`() {
//        // GIVEN
//        val offsetLimit = OffsetLimit(limit = 20)
//
//        val blogs = BlogFixture.giveMe(3)
//        val articles = blogs.flatMap { ArticleFixture.giveMe(7, it.id) }
//
//        // WHEN
//        every { articleFinder.findPublishedAfter(null, any<OffsetLimit>()) } returns articles
//        every { blogFinder.findByIds(any()) } returns blogs
//
//        val page = articleService.getArticles(cursor = null, limit = offsetLimit.limit)
//
//        // THEN
//        page.data.size.shouldBe(offsetLimit.limit)
//        page.hasNext.shouldBeTrue()
//        page.nextCursor.shouldNotBeNull()
//    }
//
//    @Test
//    fun `요청한 개수와 적거나 같은 수의 데이터가 반환되면 다음 페이지는 존재하지 않는다`() {
//        // GIVEN
//        val offsetLimit = OffsetLimit(limit = 20)
//
//        val blogs = BlogFixture.giveMe(2)
//        val articles = blogs.flatMap { ArticleFixture.giveMe(10, it.id) }
//
//        // WHEN
//        every { articleFinder.findPublishedAfter(null, any<OffsetLimit>()) } returns articles
//        every { blogFinder.findByIds(any()) } returns blogs
//
//        val page = articleService.getArticles(cursor = null, limit = offsetLimit.limit)
//
//        // THEN
//        page.data.size.shouldBe(offsetLimit.limit)
//        page.hasNext.shouldBeFalse()
//        page.nextCursor.shouldBeNull()
//    }
//
//    @Test
//    fun `데이터가 존재하지 않는 경우 빈 패이지를 반환한다`() {
//        // GIVEN
//        val offsetLimit = OffsetLimit(limit = 20)
//
//        val blogs = BlogFixture.giveMe(2)
//        val articles = blogs.flatMap { ArticleFixture.giveMe(0, it.id) }
//
//        // WHEN
//        every { articleFinder.findPublishedAfter(null, any<OffsetLimit>()) } returns articles
//        every { blogFinder.findByIds(any()) } returns blogs
//
//        val page = articleService.getArticles(cursor = null, limit = offsetLimit.limit)
//
//        // THEN
//        page.data.size.shouldBeZero()
//        page.hasNext.shouldBeFalse()
//        page.nextCursor.shouldBeNull()
//    }
//}
