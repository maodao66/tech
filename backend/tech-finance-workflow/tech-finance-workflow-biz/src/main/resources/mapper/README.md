# MyBatis XML（Dao 层）

接口放在 `com.techfinance.workflow.dao`，XML 放本目录。

```text
controller → service → service.impl → dao(+xml)
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.techfinance.workflow.dao.XxxDao">
    <!-- <select id="customQuery" resultType="...">...</select> -->
</mapper>
```
