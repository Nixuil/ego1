package com.ego.common.exception;

import com.ego.common.enums.IException;
import lombok.Data;

/**
 *
 */
@Data
public class EgoException extends RuntimeException {
    private Integer errorCode;
    private String errorMessage;

    public EgoException(IException iException) {
        this.errorCode = iException.getCode();
        this.errorMessage = iException.getMessage();
    }
}
