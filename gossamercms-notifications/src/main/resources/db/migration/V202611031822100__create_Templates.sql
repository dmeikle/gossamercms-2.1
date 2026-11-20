ALTER TABLE "SubscriptionKeys"
    ADD CONSTRAINT "uq_SubscriptionKeys_subscriptionKey" UNIQUE ("subscriptionKey");

CREATE TABLE templates (
    "id" UUID PRIMARY KEY,
    "name" VARCHAR(255),
    "templateUrl" VARCHAR(200),
    "eventKey" VARCHAR(45),
    "variables" JSONB,
    "subscriptionKey" VARCHAR(45),
    "isSubscriber" BOOLEAN,
    "priority" VARCHAR,
    "ttl" INTEGER,
    "buildCommand" VARCHAR(45),
    "createdBy" UUID,
    "updatedBy" UUID,
    "deletedBy" UUID,
    "deletedAt" TIMESTAMP,
    "createdAt" TIMESTAMP DEFAULT NOW(),
    "updatedAt" TIMESTAMP DEFAULT NOW(),
    CONSTRAINT "fk_Templates_subscriptionKey" FOREIGN KEY ("subscriptionKey") REFERENCES "SubscriptionKeys" ("subscriptionKey")
);

CREATE INDEX "idx_Templates_subscriptionKey" ON templates ("subscriptionKey");
