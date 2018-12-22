package com.microservices.user.core.crypto;

public interface UserAuthTokenGeneratorService {
    public String generate(Long userId);
    public String generate(Long userId, String sessionId);
    public Long getUserId(String token);
}
