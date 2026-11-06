CREATE TABLE groups (
    "id" UUID PRIMARY KEY,
    "createdBy" UUID,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "name" VARCHAR(45),
    "isPrivate" BOOLEAN,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
