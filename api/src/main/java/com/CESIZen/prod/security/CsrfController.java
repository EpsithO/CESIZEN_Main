package com.CESIZen.prod.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.UUID;

@RestController
public class CsrfController {
  @GetMapping("/csrf")
  public Map<String, String> csrf() {
    return Map.of(
      "headerName", "X-CSRF-TOKEN",
      "parameterName", "_csrf",
      "token", UUID.randomUUID().toString()
    );
  }
}
