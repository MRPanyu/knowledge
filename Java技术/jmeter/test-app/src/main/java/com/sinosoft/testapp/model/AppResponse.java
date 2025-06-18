package com.sinosoft.testapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppResponse<T> {

	public static final int STATUS_SUCCESS = 200;
	public static final int STATUS_ERROR = 500;

	public static <V> AppResponse<V> ok(V data) {
		return new AppResponse<V>(STATUS_SUCCESS, "success", data);
	}

	public static <V> AppResponse<V> ok() {
		return new AppResponse<V>(STATUS_SUCCESS, "success", null);
	}

	public static <V> AppResponse<V> error(String message) {
		return new AppResponse<V>(STATUS_ERROR, message, null);
	}

	private int status;

	private String message;

	private T data;

}
