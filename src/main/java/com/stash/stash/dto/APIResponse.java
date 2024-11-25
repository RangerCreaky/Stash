package com.stash.stash.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.*;
import com.stash.stash.constants.ResponseStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class APIResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -8279973432992755828L;

    private String message;
    private ResponseStatusEnum responseStatus;
    private String errorCode;
    private String errorDescription;

}
