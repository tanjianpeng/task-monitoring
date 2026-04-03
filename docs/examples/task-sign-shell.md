# Linux/Shell 调用接口示例

以下示例仅演示在 Linux 或 Shell 环境中如何调用监控接口。

## 1. 调用 `begin` 接口

```bash
#!/bin/bash

curl -X POST "http://127.0.0.1:8080/api/monitor/task/begin" \
  -H "Content-Type: application/json" \
  -d '{
    "systemCode": "ACPL",
    "taskCode": "ACPL_BAT_01",
    "bizDate": "2026-04-01",
    "actualStartTime": "2026-04-01 21:31:20",
    "sign": "new StringBuffer().append(systemCode + taskCode + bizDate + actualStartTime + System.currentTimeMillis()).toString()"
  }'
```

## 2. 调用 `end` 接口

```bash
#!/bin/bash

curl -X POST "http://127.0.0.1:8080/api/monitor/task/end" \
  -H "Content-Type: application/json" \
  -d '{
   "systemCode": "ACPL",
    "taskCode": "ACPL_BAT_01",
    "bizDate": "2026-04-01",
    "actualStartTime": "2026-04-01 21:31:20",
    "sign": "new StringBuffer().append(systemCode + taskCode + bizDate + actualStartTime + System.currentTimeMillis()).toString()"
  }'
```

## 3. 返回结果示例

```json
{
  "code": "0000",
  "total": "0",
  "message": "null",
  "level": "info",
  "data": "返回成功的ID"
}
```

## 4. 说明

1. Linux 下可直接将以上命令保存为 `.sh` 文件执行。
2. Shell 脚本调用时，只需要把请求地址、请求报文和签名值替换成真实内容。
3. `sign` 当前按你提供的规则示意为：`new StringBuffer().append(systemCode + taskCode + bizDate + actualStartTime + System.currentTimeMillis()).toString()`
