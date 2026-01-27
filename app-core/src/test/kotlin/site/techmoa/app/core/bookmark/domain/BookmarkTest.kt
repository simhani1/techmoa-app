package site.techmoa.app.core.bookmark.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class BookmarkTest {

    @Test
    fun `초기 북마크 목록은 비어있다`() {
        // GIVEN
        val bookmark = Bookmark()

        // WHEN & THEN
        bookmark.getArticles().size shouldBe 0
    }

    @Test
    fun `새로운 아티클을 북마크하면 목록에 추가된다`() {
        // GIVEN
        val bookmark = Bookmark()

        // WHEN
        bookmark.add(1)

        // THEN
        bookmark.getArticles().size shouldBe 1
    }

    @Test
    fun `북마크를 해제하면 목록에서 제거된다`() {
        // GIVEN
        val bookmark = Bookmark()

        // WHEN
        bookmark.add(1)
        bookmark.remove(1)

        // THEN
        bookmark.getArticles().size shouldBe 0
    }

    @Test
    fun `중복 북마크하면 목록에 변화없다`() {
        // GIVEN
        val bookmark = Bookmark()

        // WHEN
        bookmark.add(1)
        bookmark.add(1)

        // THEN
        bookmark.getArticles().size shouldBe 1
    }
}
