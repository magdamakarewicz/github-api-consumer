package com.enjoythecode.githubapiconsumer.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ExceptionResponseBody {

    private final int status;

    private final String message;

}