package com.techfinance.system.feign.impl;

import com.techfinance.system.client.SystemClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务间调用入口：实现 Feign Client，内部转调 service。
 * <p>
 * 调用链：Feign → FeignImpl → Service → ServiceImpl → Dao
 */
@RestController
@RequestMapping("/system")
public class SystemFeignImpl implements SystemClient {

    // 业务接口补充后注入 XxxService，在实现方法中转调即可
}
