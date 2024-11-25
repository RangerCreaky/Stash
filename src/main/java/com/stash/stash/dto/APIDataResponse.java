package com.stash.stash.dto;

import com.stash.stash.constants.ResponseStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class APIDataResponse extends APIResponse{
    public Object data;

    public APIDataResponse(String message, ResponseStatusEnum responseStatus, String errorCode, String errorDescription, Object data){
        super(message, responseStatus, errorCode, errorDescription);
        this.data = data;
    }
}
