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
    "createdAt" TIMESTAMP WITH TIME ZONE,
    "updatedBy" UUID,
    "updatedAt" TIMESTAMP WITH TIME ZONE,
    "deletedBy" UUID,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT "fk_Templates_subscriptionKey" FOREIGN KEY ("subscriptionKey") REFERENCES "SubscriptionKeys" ("subscriptionKey")
);

CREATE INDEX "idx_Templates_subscriptionKey" ON templates ("subscriptionKey");
