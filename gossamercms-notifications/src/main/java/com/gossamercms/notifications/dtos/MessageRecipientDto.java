package com.gossamercms.notifications.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRecipientDto {

    private UUID userId;

    private UUID userContextId;

    private String firstName;

    private String email;

    private String phone;

    private String locale;


    public Locale getPreferredLocale() {
        return locale == null || locale.isBlank()
                ? Locale.CANADA
                : Locale.forLanguageTag(locale);
    }
}
