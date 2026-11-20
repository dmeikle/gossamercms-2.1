CREATE TABLE "SubscriptionKeys" (
    "id" UUID PRIMARY KEY,
    "subscriptionKey" VARCHAR(45),
    "name" VARCHAR(45),
    "description" VARCHAR(200),
    "section" VARCHAR,
    "createdBy" UUID,
    "updatedBy" UUID,
    "deletedBy" UUID,
    "deletedAt" TIMESTAMP,
    "createdAt" TIMESTAMP DEFAULT NOW(),
    "updatedAt" TIMESTAMP DEFAULT NOW()

);

