package com.gossamercms.utilities.languages.dtos.responses;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LanguageResponse {
    private UUID id;
    private String code;
    private String name;

}
