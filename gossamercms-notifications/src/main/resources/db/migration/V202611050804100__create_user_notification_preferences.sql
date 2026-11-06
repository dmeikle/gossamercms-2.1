CREATE TABLE user_notification_preferences
(
    id                  UUID                     NOT NULL,
    "userContextId"     UUID                     NOT NULL,
    channel             VARCHAR(30)              NOT NULL,
    enabled             BOOLEAN                  NOT NULL DEFAULT TRUE,

    "createdBy"         UUID,
    "createdAt"         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedBy"         UUID,
    "updatedAt"         TIMESTAMP,
    "deletedBy"         UUID,
    "deletedAt"         TIMESTAMP,

    CONSTRAINT pk_user_notification_preferences
        PRIMARY KEY (id),

    CONSTRAINT ux_user_notification_preferences_context_channel
        UNIQUE ("userContextId", channel)
);