package com.sit.qb.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiSuccessResponseDto<T> {

	private LocalDateTime timestamp;

	private int status;

	private String message;

	private T data;

	private String path;
	
	
}
