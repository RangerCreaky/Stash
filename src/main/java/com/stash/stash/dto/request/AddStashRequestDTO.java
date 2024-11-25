package com.stash.stash.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddStashRequestDTO {

    @NotNull(message = "Stash name is required")
    private String name;
    private String description;
    private String category;
}
