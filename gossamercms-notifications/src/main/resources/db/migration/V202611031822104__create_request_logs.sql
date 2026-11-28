CREATE TABLE if not exists request_logs (
    "id" UUID PRIMARY KEY,
    "serverKey" UUID,
    "eventKey" VARCHAR(45),
    "requestBody" JSONB,
    "processingTime" BIGINT,
    "correlationId" UUID,
    "logLevel" VARCHAR(7),
    "createdAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    "updatedAt" TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
