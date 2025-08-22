package com.example.loginsystem.dto;

import java.time.LocalDateTime;

/**
 * API统一响应格式
 * @param <T> 响应数据类型
 */
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private int code;

    // 私有构造函数
    private ApiResponse(boolean success, String message, T data, int code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }

    // 成功响应 - 带数据
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data, 200);
    }

    // 成功响应 - 自定义消息和数据
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, 200);
    }

    // 成功响应 - 只有消息
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, 200);
    }

    // 错误响应
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, 400);
    }

    // 错误响应 - 自定义状态码
    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>(false, message, null, code);
    }

    // 服务器错误响应
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(false, message, null, 500);
    }

    // 未授权响应
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(false, message, null, 401);
    }

    // 禁止访问响应
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(false, message, null, 403);
    }

    // 资源未找到响应
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, message, null, 404);
    }

    // Getter方法
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getCode() {
        return code;
    }

    // Setter方法（用于序列化框架）
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", code=" + code +
                '}';
    }
}