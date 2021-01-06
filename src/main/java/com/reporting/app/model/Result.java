package com.reporting.app.model;

import lombok.Data;

@Data
public class Result<T> {
  public static final String TECHNICAL_ERROR = "TE-101";
  public static final String BUSINESS_ERROR = "BE-101";
  public static final String SUCCESS_CODE = "100";

  private T data;
  private String message;
  private String code;

  public Result() {
    this.code = SUCCESS_CODE;
  }

  public Result(String message, String code) {
    this.message = message;
    this.code = code;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public static String getTechnicalError() {
    return TECHNICAL_ERROR;
  }

  public void setTechnicalError(String message) {
    this.setError(TECHNICAL_ERROR, message);
  }

  public void setBusinessError(String message) {
    this.setError(BUSINESS_ERROR, message);
  }

  public void setError(String code, String message) {
    this.setCode(code);
    this.setMessage(message);
  }
}
