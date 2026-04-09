# 本地大屏三个查询接口联调说明

1. 顶部统计数量接口
2. 系统信息接口
3. 任务状态信息接口

统一说明：

1. 接口前缀：`/api`
2. 返回结构统一为 `ResultDto<T>`
3. `code = "0000"` 表示成功
4. 时间字段格式统一为 `yyyy-MM-dd HH:mm:ss`
5. 三个本地查询接口的业务日期都由后端优先取实例表最新一条 `bizDate`，实例表为空时回退到昨天日期 `yyyy-MM-dd`

---

## 1. 顶部统计数量接口

- 请求方式：`GET`
- 请求路径：`/api/monitor/local/queryStatistics`
- 请求参数：无

请求字段说明：

1. `method`：请求方式
2. `url`：接口请求地址
3. `contentType`：请求内容类型
4. `bizDate`：无需前端传值，由后端优先取实例表最新一条业务日期，实例表为空时使用昨天日期

请求示例：

```json
{
  "method": "GET",
  "url": "/api/monitor/local/queryStatistics",
  "contentType": "application/json"
}
```

响应字段说明：

1. `code`：返回码，`0000` 表示成功
2. `message`：返回信息
3. `data`：顶部统计字符串对象
4. `data.systemCount`：监控系统数
5. `data.taskCount`：今日总任务数
6. `data.notStartCount`：待执行
7. `data.runningCount`：执行中
8. `data.successCount`：成功
9. `data.failedCount`：失败

响应示例：

```json
{
  "code": "0000",
  "message": "success",
  "data": {
    "systemCount": "7",
    "taskCount": "6",
    "notStartCount": "1",
    "runningCount": "1",
    "successCount": "2",
    "failedCount": "1"
  }
}
```

## 2. 系统信息接口

- 请求方式：`GET`
- 请求路径：`/api/monitor/local/querySystems`
- 请求参数：`systemName`

请求字段说明：

1. `method`：请求方式
2. `url`：接口请求地址
3. `contentType`：请求内容类型
4. `bizDate`：无需前端传值，由后端优先取实例表最新一条业务日期，实例表为空时使用昨天日期
5. `query.systemName`：系统名称，预留给前端做搜索筛选，支持模糊匹配；不传时查询全部系统卡片

请求示例：

```json
{
  "method": "GET",
  "url": "/api/monitor/local/querySystems?systemName=核算",
  "contentType": "application/json",
  "query": {
    "systemName": "核算"
  }
}
```

响应字段说明：

1. `code`：返回码，`0000` 表示成功
2. `message`：返回信息
3. `data`：系统信息卡片列表
4. `data[].systemCode`：系统编码
5. `data[].systemName`：系统名称
6. `data[].ownerName`：系统负责人
7. `data[].supervisorName`：监督人
8. `data[].notStartCount`：待执行数量
9. `data[].runningCount`：执行中数量
10. `data[].successCount`：成功数量
11. `data[].failedCount`：失败数量
12. `data[].totalCount`：总数，等于待执行、执行中、成功、失败四种状态数量之和

响应示例：

```json
{
  "code": "0000",
  "message": "success",
  "data": [
    {
      "systemCode": "HXPT",
      "systemName": "核心系统",
      "ownerName": "王臣",
      "supervisorName": "刘日旭",
      "notStartCount": 7,
      "runningCount": 0,
      "successCount": 10,
      "failedCount": 1,
      "totalCount": 18
    },
    {
      "systemCode": "ACCT",
      "systemName": "核算平台",
      "ownerName": "温强",
      "supervisorName": "何锋",
      "notStartCount": 2,
      "runningCount": 1,
      "successCount": 5,
      "failedCount": 0,
      "totalCount": 8
    }
  ]
}
```

## 3. 任务状态信息接口

- 请求方式：`GET`
- 请求路径：`/api/monitor/local/queryTaskStatus`
- 请求参数：无
- 返回说明：
  任务 list 已按依赖关系尽量做顺序整理，前置任务排前面，末尾任务排后面，方便前端直接渲染链路。

请求字段说明：

1. `method`：请求方式
2. `url`：接口请求地址
3. `contentType`：请求内容类型
4. `bizDate`：无需前端传值，由后端优先取实例表最新一条业务日期，实例表为空时使用昨天日期

请求示例：

```json
{
  "method": "GET",
  "url": "/api/monitor/local/queryTaskStatus",
  "contentType": "application/json"
}
```

响应字段说明：

1. `code`：返回码，`0000` 表示成功
2. `message`：返回信息
3. `data`：任务状态返回对象
4. `data.taskList`：任务节点列表
5. `data.依赖关系节点list`：依赖关系节点列表
6. `data.taskList[].bizDate`：业务日期
7. `data.taskList[].runTimes`：重跑次数
8. `data.taskList[].taskCode`：任务编码
9. `data.taskList[].taskName`：任务名称
10. `data.taskList[].systemCode`：所属系统编码
11. `data.taskList[].systemName`：所属系统名称
12. `data.taskList[].ownerName`：任务负责人
13. `data.taskList[].supervisorName`：监督人
14. `data.taskList[].preRequisiteProd`：前置依赖产物
15. `data.taskList[].theBatchProd`：本批处理任务产出物
16. `data.taskList[].posX`：任务在大屏上的横坐标
17. `data.taskList[].posY`：任务在大屏上的纵坐标
18. `data.taskList[].defaultCostMinutes`：默认耗时，单位分钟
19. `data.taskList[].frequency`：频率
20. `data.taskList[].taskIsFlag`：任务表中的是否大屏展示字段，0是，1否
21. `data.taskList[].remark`：任务备注
22. `data.taskList[].resultStatus`：当前运行结果状态；未回传开始时间为 `NOTSTART`，开始但未超过最晚开始时间为 `RUNNING`，开始晚于最晚开始时间或执行中超过预计结束时间为 `DELAYED`
23. `data.taskList[].planStartTime`：计划开始时间
24. `data.taskList[].latestStartTime`：最晚开始时间，允许延迟分钟数由后端按任务编码查询后计算
25. `data.taskList[].planEndTime`：计划结束时间
26. `data.taskList[].actualStartTime`：实际开始时间，格式 `HH:mm:ss`
27. `data.taskList[].actualEndTime`：实际结束时间，格式 `HH:mm:ss`
28. `data.taskList[].currentCostMinutes`：当前耗时，格式 `mm:ss`；结束任务按“结束回调时间 - 开始回调时间”计算，待执行任务为空
29. `data.taskList[].avgCostMinutes`：历史平均耗时，格式 `mm:ss`；平日按往前 30 个非月底业务日统计，月底按往前 6 个自然月底业务日统计
30. `data.taskList[].predictEndTime`：预计结束时间；收到开始时间后按“开始时间 + 历史平均耗时”计算，未开始时通常为空
31. `data.taskList[].delayedFlag`：是否延迟，1是，0否；包含开始时间超过最晚开始时间、执行中超过预计结束时间、已结束但晚于预计结束时间
32. `data.taskList[].timeoutFlag`：是否超过预计结束时间，1是，0否；该字段仅作为标记，结果状态统一并入 `DELAYED`
33. `data.taskList[].resultStatusName`：执行结果状态中文说明，例如待执行、执行中、成功、失败、延迟
34. `data.taskList[].isFlag`：实例表中的是否展示字段，1否，0是
35. `data.依赖关系节点list[].preTaskCode`：前置任务编码
36. `data.依赖关系节点list[].taskCode`：当前任务编码
37. `data.依赖关系节点list[].direction`：箭头方向，常见值为 `TOP`、`BOTTOM`、`LEFT`、`RIGHT`

响应示例：

```json
{
  "code": "0000",
  "message": "success",
  "data": {
    "taskList": [
      {
      "bizDate": "20260407",
      "runTimes": 0,
      "taskCode": "BDPS_BAT_01",
      "taskName": "报表平台取数任务",
      "systemCode": "BDPS",
      "systemName": "报表平台",
      "ownerName": "李涛",
      "supervisorName": "何锋",
      "preRequisiteProd": "",
      "theBatchProd": "报表平台基础数据",
      "posX": 120,
      "posY": 80,
      "defaultCostMinutes": 20,
      "frequency": "D",
      "taskIsFlag": "0",
      "remark": "报表平台前置任务",
      "resultStatus": "SUCCESS",
      "resultStatusName": "成功",
      "planStartTime": "2026-04-07 20:00:00",
      "latestStartTime": "2026-04-07 20:10:00",
      "planEndTime": "2026-04-07 20:20:00",
      "actualStartTime": "20:00:03",
      "actualEndTime": "20:15:26",
      "currentCostMinutes": "15:23",
      "avgCostMinutes": "18:00",
      "predictEndTime": "2026-04-07 20:18:03",
      "delayedFlag": 0,
      "timeoutFlag": 0,
      "isFlag": "0"
    },
    {
      "bizDate": "20260407",
      "runTimes": 0,
      "taskCode": "ACPL_BAT_01",
      "taskName": "核算平台日终批处理任务",
      "systemCode": "ACPL",
      "systemName": "核算平台",
      "ownerName": "王臣",
      "supervisorName": "谢卫华",
      "preRequisiteProd": "报表平台基础数据",
      "theBatchProd": "核算平台日终结果文件",
      "posX": 320,
      "posY": 80,
      "defaultCostMinutes": 35,
      "frequency": "D",
      "taskIsFlag": "0",
      "remark": "核心日终主任务",
      "resultStatus": "RUNNING",
      "resultStatusName": "执行中",
      "planStartTime": "2026-04-07 21:30:00",
      "latestStartTime": "2026-04-07 21:40:00",
      "planEndTime": "2026-04-07 22:05:00",
      "actualStartTime": "21:31:20",
      "actualEndTime": null,
      "currentCostMinutes": "22:00",
      "avgCostMinutes": "35:00",
      "predictEndTime": "2026-04-07 22:06:20",
      "delayedFlag": 0,
      "timeoutFlag": 0,
      "isFlag": "0"
    },
    {
      "bizDate": "20260407",
      "runTimes": 0,
      "taskCode": "ACPL_BAT_02",
      "taskName": "核算平台卸数任务",
      "systemCode": "ACPL",
      "systemName": "核算平台",
      "ownerName": "王臣",
      "supervisorName": "谢卫华",
      "preRequisiteProd": "核算平台日终结果文件",
      "theBatchProd": "核算平台卸数文件",
      "posX": 520,
      "posY": 80,
      "defaultCostMinutes": 20,
      "frequency": "D",
      "taskIsFlag": "0",
      "remark": "核算平台卸数子任务",
      "resultStatus": "NOTSTART",
      "resultStatusName": "待执行",
      "planStartTime": "2026-04-07 22:10:00",
      "latestStartTime": "2026-04-07 22:20:00",
      "planEndTime": "2026-04-07 22:30:00",
      "actualStartTime": null,
      "actualEndTime": null,
      "currentCostMinutes": null,
      "avgCostMinutes": "20:00",
      "predictEndTime": null,
      "delayedFlag": 0,
      "timeoutFlag": 0,
      "isFlag": "0"
    },
    {
      "bizDate": "20260407",
      "runTimes": 0,
      "taskCode": "NWLS_BAT_01",
      "taskName": "新网贷系统卸数任务",
      "systemCode": "NWLS",
      "systemName": "新网贷系统",
      "ownerName": "温强",
      "supervisorName": "何锋",
      "preRequisiteProd": "核算平台卸数文件",
      "theBatchProd": "新网贷日终数据",
      "posX": 720,
      "posY": 80,
      "defaultCostMinutes": 45,
      "frequency": "D",
      "taskIsFlag": "0",
      "remark": "新网贷接续任务",
      "resultStatus": "DELAYED",
      "resultStatusName": "延迟",
      "planStartTime": "2026-04-07 22:35:00",
      "latestStartTime": "2026-04-07 22:45:00",
      "planEndTime": "2026-04-07 23:20:00",
      "actualStartTime": null,
      "actualEndTime": null,
      "currentCostMinutes": null,
      "avgCostMinutes": "45:00",
      "predictEndTime": null,
      "delayedFlag": 1,
      "timeoutFlag": 0,
      "isFlag": "0"
    }
    ],
    "dependencyNodeList": [
      {
        "preTaskCode": "BDPS_BAT_01",
        "taskCode": "ACPL_BAT_01",
        "direction": "RIGHT"
      },
      {
        "preTaskCode": "ACPL_BAT_01",
        "taskCode": "ACPL_BAT_02",
        "direction": "RIGHT"
      },
      {
        "preTaskCode": "ACPL_BAT_02",
        "taskCode": "NWLS_BAT_01",
        "direction": "RIGHT"
      }
    ]
  }
}
```
