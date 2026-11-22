ALTER TABLE outbox_messages ADD platform VARCHAR(20) NOT NULL DEFAULT 'DISCORD';
UPDATE outbox_messages o JOIN webhook w ON w.webhook_id = o.webhook_id
SET o.platform = w.platform WHERE o.webhook_id IS NOT NULL;

CREATE TABLE webhook_platform_retry (
    platform VARCHAR(20) PRIMARY KEY,
    retry_at DATETIME(6) NOT NULL
);
INSERT INTO webhook_platform_retry (platform, retry_at)
SELECT 'DISCORD', retry_at FROM webhook_dispatch_clock WHERE id = 1;
DROP TABLE webhook_dispatch_clock;
