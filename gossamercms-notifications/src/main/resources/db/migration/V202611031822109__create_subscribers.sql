CREATE TABLE subscribers (
    "id" UUID PRIMARY KEY,
    "createdBy" UUID,
    "updatedBy" UUID,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "deletedBy" UUID,
    "accountMappingsId" UUID,
    "subscriptionKeysId" UUID,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
