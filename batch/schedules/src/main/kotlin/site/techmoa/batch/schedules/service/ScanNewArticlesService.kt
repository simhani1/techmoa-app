package site.techmoa.batch.schedules.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.techmoa.batch.schedules.repository.ArticleRepository
import site.techmoa.batch.schedules.repository.WebhookRepository
import site.techmoa.batch.schedules.usecase.LastScannedArticleUseCase
import site.techmoa.batch.schedules.usecase.NewArticlesEventUseCase

@Service
class ScanNewArticlesService(
    private val lastScannedArticleUseCase: LastScannedArticleUseCase,
    private val newArticlesEventUseCase: NewArticlesEventUseCase,
    private val articleRepository: ArticleRepository,
    private val webhookRepository: WebhookRepository
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    fun execute() {
        //// BIZ 로직
        // 1. 마지막 스캔 id 조회
        val lastScanned = lastScannedArticleUseCase.getLastScannedArticle()

        // 2. 새로운 아티클 스캔
        val newArticles = articleRepository.findByIdGreaterThan(lastScanned.articleId)
        if (newArticles.isEmpty()) return  // 2-1. 제약 ( 새로운 아티클 없으면 중단 )
        newArticles.sortedBy { it.articleId }  // 2-2. 최신 저장순으로 정렬

        // 3. 웹훅 url 조회
        val webhooks = webhookRepository.getValidWebhook()
        if (webhooks.isEmpty()) return  // 3-1. 제약 ( 웹훅이 없으면 중단 )

        // 4. 마지막 스캔 id 저장
        lastScannedArticleUseCase.sync(newArticles.last().articleId)
        //// BIZ 로직

        // 5. 커밋 직전 아웃박스 테이블 기록 (원자성 보장) -> 별도 유스케이스를 분리하여 Before Commit 이벤트 리슨
        newArticlesEventUseCase.publish(newArticles, webhooks)
    }
}
