# Linux/Shell 调用接口示例

以下示例仅演示在 Linux 或 Shell 环境中如何调用监控接口。

## 1. 调用 `start` 接口

```bash
#!/bin/bash

curl -X POST "http://127.0.0.1:8080/api/monitor/task/report" \
  -H "Content-Type: application/json" \
  -d '{
    "systemCode": "ACPL",
    "taskCode": "ACPL_BAT_01",
    "bizDate": "2026-04-01",
    "status": "start",
    "requestTime": "2026-04-01 21:31:20",
    "startTime": "2026-04-01 21:31:20",
    "sign": "md5(所有参与签名参数拼接后的字符串)"
  }'
```

## 2. 调用 `stop` 接口

```bash
#!/bin/bash

curl -X POST "http://127.0.0.1:8080/api/monitor/task/report" \
  -H "Content-Type: application/json" \
  -d '{
    "systemCode": "ACPL",
    "taskCode": "ACPL_BAT_01",
    "bizDate": "2026-04-01",
    "status": "stop",
    "requestTime": "2026-04-01 21:49:58",
    "endTime": "2026-04-01 21:49:58",
    "sign": "md5(所有参与签名参数拼接后的字符串)"
  }'
```

## 3. 调用 `fail` 接口

```bash
#!/bin/bash

curl -X POST "http://127.0.0.1:8080/api/monitor/task/report" \
  -H "Content-Type: application/json" \
  -d '{
    "systemCode": "ACPL",
    "taskCode": "ACPL_BAT_01",
    "bizDate": "2026-04-01",
    "status": "fail",
    "requestTime": "2026-04-01 21:49:58",
    "endTime": "2026-04-01 21:49:58",
    "sign": "md5(所有参与签名参数拼接后的字符串)"
  }'
```

## 4. 返回结果示例

```json
{
  "code": "0000",
  "total": "0",
  "message": "null",
  "level": "info",
  "data": "返回成功的ID"
}
```

## 5. 说明

1. Linux 下可直接将以上命令保存为 `.sh` 文件执行。
2. Shell 脚本调用时，只需要把请求地址、请求报文和签名值替换成真实内容。
3. `status` 支持 `start`、`stop`、`restart`、`fail` 四种取值。
4. `stop` 表示成功结束，`fail` 表示失败结束。
5. `sign` 需要按业务系统约定，将参与签名的参数按固定顺序拼接为字符串后再做 MD5。
