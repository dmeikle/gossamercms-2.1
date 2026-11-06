CREATE TABLE message_logs (
    "id" UUID PRIMARY KEY,
    "createdBy" UUID,
    "updatedBy" UUID,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "deletedBy" UUID,
    "messagesId" UUID,
    "result" VARCHAR(10),
    "message" VARCHAR(200),
    "notificationTarget" VARCHAR(45),
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT "fk_message_logs_messagesId" FOREIGN KEY ("messagesId") REFERENCES "messages" ("id")
);
CREATE INDEX "idx_message_logs_messagesId" ON "message_logs" ("messagesId");
