CREATE TABLE "SubscriptionKeys" (
    "id" UUID PRIMARY KEY,
    "subscriptionKey" VARCHAR(45),
    "name" VARCHAR(45),
    "description" VARCHAR(200),
    "section" VARCHAR,
    "createdBy" UUID,
    "createdAt" TIMESTAMP WITH TIME ZONE,
    "updatedBy" UUID,
    "updatedAt" TIMESTAMP WITH TIME ZONE,
    "deletedBy" UUID,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW()

);

