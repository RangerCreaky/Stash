package com.stash.stash.dto.request;

import com.stash.stash.constants.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddUserToStashRequestDTO {
    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Stash id is required")
    private Long stashId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    private RoleEnum role;
}
