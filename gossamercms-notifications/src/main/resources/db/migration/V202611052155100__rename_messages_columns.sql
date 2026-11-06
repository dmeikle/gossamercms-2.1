ALTER TABLE messages RENAME COLUMN "accountId" TO "userContextId";
ALTER TABLE messages RENAME COLUMN "templatesId" TO "templateId";

ALTER TABLE messages
    ADD CONSTRAINT "fk_messages_userContextId"
        FOREIGN KEY ("userContextId") REFERENCES user_contexts (id);

CREATE INDEX "idx_messages_userContextId"
    ON messages ("userContextId");

-- Optional: align existing names with the renamed column.
ALTER INDEX "idx_messages_templatesId"
    RENAME TO "idx_messages_templateId";

ALTER TABLE messages
    RENAME CONSTRAINT "fk_messages_templatesId"
    TO "fk_messages_templateId";