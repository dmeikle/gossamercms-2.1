CREATE TABLE user_messages (
    "id" UUID PRIMARY KEY,
    "createdBy" UUID,
    "updatedBy" UUID,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "deletedBy" UUID,
    "accountMappingsId" UUID,
    "comments" VARCHAR(1000),
    "status" VARCHAR(9),
    "answeredAt" TIMESTAMP WITH TIME ZONE,
    "answeredBy" UUID,
    "topic" VARCHAR(255),
    "username" VARCHAR(45),
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
