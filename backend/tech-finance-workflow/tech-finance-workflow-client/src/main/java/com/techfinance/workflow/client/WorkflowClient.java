package com.techfinance.workflow.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * workflow 服务 Feign 契约。
 * <p>
 * 消费方：注入本接口发起调用<br>
 * 提供方：{@code WorkflowFeignImpl} 实现本接口并转调 Service
 */
@FeignClient(name = "tech-finance-workflow", contextId = "workflowClient", path = "/workflow")
public interface WorkflowClient {
}
