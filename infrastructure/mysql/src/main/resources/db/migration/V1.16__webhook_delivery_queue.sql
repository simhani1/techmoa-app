ALTER TABLE article MODIFY created_at DATETIME(6) NOT NULL;
ALTER TABLE webhook ADD subscribed_at DATETIME(6) NULL;
UPDATE webhook SET subscribed_at = created_at;
ALTER TABLE webhook MODIFY subscribed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

ALTER TABLE outbox_messages ADD webhook_id BIGINT NULL;
-- Preserve historical outcomes. Map the oldest eligible pending delivery per pair.
CREATE TEMPORARY TABLE pending_webhook_backfill AS
SELECT MIN(o.outbox_message_id) AS message_id, MIN(w.webhook_id) AS webhook_id
FROM outbox_messages o
JOIN webhook w ON BINARY w.url = BINARY JSON_UNQUOTE(JSON_EXTRACT(o.payload, '$.webhookUrl'))
JOIN article a ON a.article_id = o.aggregate_id AND a.created_at > w.subscribed_at
WHERE o.status = 'PENDING'
GROUP BY BINARY w.url, o.aggregate_id;

UPDATE outbox_messages o JOIN pending_webhook_backfill b ON o.outbox_message_id = b.message_id
SET o.webhook_id = b.webhook_id;
DROP TEMPORARY TABLE pending_webhook_backfill;

UPDATE outbox_messages SET status = 'FAIL', last_error_message = 'Legacy delivery requires manual review'
WHERE webhook_id IS NULL AND status IN ('PENDING', 'PUBLISHING');

ALTER TABLE outbox_messages
    ADD UNIQUE KEY uq_delivery (webhook_id, aggregate_id),
    ADD INDEX ix_pending_delivery (status, outbox_message_id),
    ADD INDEX ix_webhook_pending_delivery (webhook_id, status, outbox_message_id);

CREATE TABLE webhook_retry (
    webhook_id BIGINT PRIMARY KEY,
    retry_at DATETIME(6) NOT NULL
);
CREATE TABLE webhook_dispatch_clock (
    id BIGINT PRIMARY KEY,
    retry_at DATETIME(6) NOT NULL
);
INSERT INTO webhook_dispatch_clock VALUES (1, CURRENT_TIMESTAMP(6));
