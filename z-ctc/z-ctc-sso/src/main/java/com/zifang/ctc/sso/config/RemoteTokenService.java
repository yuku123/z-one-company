package com.zifang.ctc.sso.config;


import com.zifang.ctc.sso.model.UserInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

public class RemoteTokenService implements TokenService {

    @Resource
    private SsoProperties ssoProperties;

    @Resource
    private RestTemplate restTemplate;

    public RemoteTokenService(SsoProperties ssoProperties, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.ssoProperties = ssoProperties;
    }

    @Override
    public UserInfo verifyToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<UserInfo> response = restTemplate.exchange(
                    ssoProperties.getAuthServerUrl(),
                    HttpMethod.GET,
                    requestEntity,
                    UserInfo.class
            );

            return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
        } catch (Exception e) {
            // 验证失败
            return null;
        }
    }
}
