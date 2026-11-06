CREATE TABLE messages (
    "id" UUID PRIMARY KEY,
    "createdBy" UUID,
    "updatedBy" UUID,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "deletedBy" UUID,
    "subject" VARCHAR(250),
    "message" VARCHAR(2000),
    "accountId" UUID,
    "viewedAt" TIMESTAMP WITH TIME ZONE,
    "templatesId" UUID,
    "messageType" VARCHAR(7),
    "requestLogsId" UUID,
    "decayDate" TIMESTAMP WITH TIME ZONE,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT "fk_messages_templatesId" FOREIGN KEY ("templatesId") REFERENCES "templates" ("id"),
    CONSTRAINT "fk_messages_requestLogsId" FOREIGN KEY ("requestLogsId") REFERENCES "request_logs" ("id")
);
CREATE INDEX "idx_messages_templatesId" ON "messages" ("templatesId");
CREATE INDEX "idx_messages_requestLogsId" ON "messages" ("requestLogsId");
