CREATE TABLE contact_forms (
    "id" UUID PRIMARY KEY,
    "name" VARCHAR(255),
    "slug" VARCHAR(255),
    "description" VARCHAR,
    "status" VARCHAR(50),
    "successMessage" VARCHAR,
    "notificationRecipients" JSONB,
    "sendConfirmationEmail" BOOLEAN,
    "confirmationEmailSubject" VARCHAR(255),
    "confirmationEmailTemplate" VARCHAR,
    "allowAttachments" BOOLEAN,
    "maximumAttachments" INTEGER,
    "maximumAttachmentSize" INTEGER,
    "settings" JSONB,
    "createdBy" UUID,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT "fk_contact_forms_createdBy" FOREIGN KEY ("createdBy") REFERENCES "users" ("id")
);
CREATE INDEX "idx_contact_forms_createdBy" ON "contact_forms" ("createdBy");
