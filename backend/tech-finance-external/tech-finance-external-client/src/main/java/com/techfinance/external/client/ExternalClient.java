package com.techfinance.external.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * external 服务 Feign 契约。
 * <p>
 * 消费方：注入本接口发起调用<br>
 * 提供方：{@code ExternalFeignImpl} 实现本接口并转调 Service
 */
@FeignClient(name = "tech-finance-external", contextId = "externalClient", path = "/external")
public interface ExternalClient {
}
