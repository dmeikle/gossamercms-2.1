package com.gossamercms.users.api.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDeviceResponse {
    private UUID id;
    private UUID userId;
    private String firebaseToken;
    private String platform;
    private String deviceId;
    private String appVersion;
    private Boolean active;
    private Instant lastSeenAt;
    private Instant createdAt;
    private Instant updatedAt;
}
