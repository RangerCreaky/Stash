package com.stash.stash.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddLinkRequestDTO {
    @NotNull(message = "Name cannot be null")
    private String name;
    private String description;
    @URL
    @NotNull(message = "Link cannot be null")
    private String link;
    private String note;
}
