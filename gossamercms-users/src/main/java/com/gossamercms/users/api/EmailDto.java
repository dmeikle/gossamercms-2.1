package com.gossamercms.users.api;


import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.users.domain.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class EmailDto  implements DtoWithId {

    private UUID id;
    private UUID userContextId;
    private String email;
    private boolean primary;
    private String status;     // ACTIVE, VERIFIED, UNVERIFIED
    private Instant createdAt;

    public Email toEntity() {
        return Email.builder()
                .id(id)
                .userContextId(userContextId)
                .email(email)
                .primary(primary)
                .status(status)
                .createdAt(createdAt)
                .build();
    }
}
