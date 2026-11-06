CREATE TABLE reminders (
    "id" UUID PRIMARY KEY,
    "createdBy" UUID,
    "updatedBy" UUID,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "deletedBy" UUID,
    "reminderTypesId" INTEGER,
    "accountMappingsId" UUID,
    "notifyAt" TIMESTAMP WITH TIME ZONE,
    "notifyAtLabel" VARCHAR(100),
    "notificationSent" BOOLEAN,
    "proxyId" UUID,
    "scheduleName" VARCHAR(100),
    "sms" INTEGER,
    "email" INTEGER,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT "fk_reminders_reminderTypesId" FOREIGN KEY ("reminderTypesId") REFERENCES "reminder_types" ("id")
);
CREATE INDEX "idx_reminders_reminderTypesId" ON "reminders" ("reminderTypesId");
