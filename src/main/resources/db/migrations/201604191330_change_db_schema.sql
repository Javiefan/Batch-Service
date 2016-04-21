DROP TABLE IF EXISTS fail_doc_log;
DROP TABLE IF EXISTS task_doc_log;

CREATE TABLE task_doc_log (
  id               BIGSERIAL PRIMARY KEY NOT NULL,
  tenant_id        UUID,
  resource_id      CHARACTER VARYING     NOT NULL,
  resource_type    CHARACTER VARYING     NOT NULL,
  phase            CHARACTER VARYING(16),
  retry_time       INTEGER,
  action_result    CHARACTER VARYING(16),
  message          CHARACTER VARYING,
  action_timestamp TIMESTAMP             NOT NULL DEFAULT 'now()'
);
CREATE INDEX task_doc_log_tenant_id_index ON task_doc_log (tenant_id);
CREATE INDEX task_doc_log_document_id_index ON task_doc_log (resource_id);
CREATE INDEX task_doc_log_action_timestamp_index ON task_doc_log (action_timestamp);

CREATE TABLE fail_doc_log
(
  id             BIGSERIAL PRIMARY KEY NOT NULL,
  tenant_id      UUID,
  resource_id    CHARACTER VARYING     NOT NULL,
  resource_type  CHARACTER VARYING     NOT NULL,
  phase          VARCHAR(16),
  task_id        BIGINT,
  message        VARCHAR,
  fail_timestamp TIMESTAMP             NOT NULL DEFAULT 'now()',
  CONSTRAINT fail_doc_log_task_doc_log_id_fk FOREIGN KEY (task_id) REFERENCES task_doc_log (id)
);
CREATE INDEX fail_doc_log_tenant_id_index ON fail_doc_log (tenant_id);
CREATE INDEX fail_doc_log_document_id_index ON fail_doc_log (resource_id);
CREATE INDEX fail_doc_log_task_id_index ON fail_doc_log (task_id);

