package com.techfinance.workflow.feign.impl;

import com.techfinance.workflow.client.WorkflowClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务间调用入口：实现 Feign Client，内部转调 service。
 * <p>
 * 调用链：Feign → FeignImpl → Service → ServiceImpl → Dao
 */
@RestController
@RequestMapping("/workflow")
public class WorkflowFeignImpl implements WorkflowClient {
}
