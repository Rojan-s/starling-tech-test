package com.starling.roundup.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/** Utility class for creating HTTP-related objects. */
public class HttpUtils {

  /**
   * Creates HttpHeaders with the necessary headers for API requests.
   *
   * @param accessToken The access token for authorization.
   * @return A HttpHeaders object with pre-configured headers.
   */
  public static HttpHeaders createHeaders(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/json");
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("User-Agent", "Rojan Siva");
    headers.set("Content-Type", "application/json");

    return headers;
  }
}