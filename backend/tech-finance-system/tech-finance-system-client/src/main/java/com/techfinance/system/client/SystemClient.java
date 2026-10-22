package com.techfinance.system.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * system 服务 Feign 契约。
 * <p>
 * 消费方：注入本接口发起调用<br>
 * 提供方：{@code SystemFeignImpl} 实现本接口并转调 Service
 */
@FeignClient(name = "tech-finance-system", contextId = "systemClient", path = "/system")
public interface SystemClient {
    // 业务方法后续补充，需与 FeignImpl / Service 保持一致
}
