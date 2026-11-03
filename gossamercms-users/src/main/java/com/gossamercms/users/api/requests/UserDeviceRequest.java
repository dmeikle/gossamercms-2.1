package com.gossamercms.users.api.requests;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDeviceRequest {
    private UUID userId;
    private String firebaseToken;
    private String platform;
    private String deviceId;
    private String appVersion;
    private Boolean active;
}
