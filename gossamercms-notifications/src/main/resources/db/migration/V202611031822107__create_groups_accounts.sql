CREATE TABLE groups_accounts (
    "id" UUID PRIMARY KEY,
    "deletedAt" TIMESTAMP WITH TIME ZONE,
    "groupsId" UUID,
    "accountsId" UUID,
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT "fk_groups_accounts_groupsId" FOREIGN KEY ("groupsId") REFERENCES "groups" ("id")
);
CREATE INDEX "idx_groups_accounts_groupsId" ON "groups_accounts" ("groupsId");
