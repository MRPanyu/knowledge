package com.sinosoft.testapp.util;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private static ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/** 对象转换成JSON字符串 */
	public static String stringify(Object obj) {
		try {
			String json = objectMapper.writeValueAsString(obj);
			return json;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/** JSON字符串解析成对象 */
	public static <T> T parse(String json, Class<T> cls) {
		try {
			T obj = objectMapper.readValue(json, cls);
			return obj;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/** JSON字符串解析成对象，支持如泛型List等的类型绑定 */
	public static <T> T parse(String json, TypeReference<T> typeReference) {
		try {
			T obj = objectMapper.readValue(json, typeReference);
			return obj;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/** JSON字符串解析成Map对象 */
	public static Map<String, Object> parseMap(String json) {
		return parse(json, new TypeReference<LinkedHashMap<String, Object>>() {
		});
	}

	/** JSON字符串解析成JsonNode对象 */
	public static JsonNode parseNode(String json) {
		try {
			return objectMapper.readTree(json);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private JsonUtils() {
	}
}
