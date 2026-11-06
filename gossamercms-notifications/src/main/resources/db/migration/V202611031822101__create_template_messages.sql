CREATE TABLE template_messages (
    "id" UUID PRIMARY KEY,
    "createdBy" UUID,
    "updatedBy" UUID,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "deletedBy" UUID,
    "templatesId" UUID,
    "locale" VARCHAR(5),
    "subject" VARCHAR(100),
    "pushTitle" VARCHAR(255),
    "pushBody" VARCHAR(600),
    "sms" VARCHAR(200),
    "htmlBody" VARCHAR,
    "textBody" VARCHAR,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT "fk_template_messages_templatesId" FOREIGN KEY ("templatesId") REFERENCES "templates" ("id")
);
CREATE INDEX "idx_template_messages_templatesId" ON "template_messages" ("templatesId");
