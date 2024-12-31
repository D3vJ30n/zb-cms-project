package com.zerobase.cms.user.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseDto<T> {
    private String message;
    private T data;

    public ResponseDto(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseDto<T> success(String message, T data) {
        return new ResponseDto<>(message, data);
    }

    public static <T> ResponseDto<T> success(String message) {
        return new ResponseDto<>(message, null);
    }

    public static ResponseDto<String> fail(String message) {
        return new ResponseDto<>("fail", null);
    }
}
