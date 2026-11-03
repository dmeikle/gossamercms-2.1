package com.gossamercms.users.api.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {
    private String firstname;
    private String lastname;
}
