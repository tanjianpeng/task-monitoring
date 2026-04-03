# 主数据维护接口

## 系统维护

### 1. 查询系统列表

- `GET /api/manage/systems`

### 2. 查询系统详情

- `GET /api/manage/systems/{systemCode}`

### 3. 新增系统

- `POST /api/manage/systems`

### 4. 修改系统

- `PUT /api/manage/systems/{systemCode}`

### 5. 删除系统

- `DELETE /api/manage/systems/{systemCode}`

说明：

1. 删除系统前会校验该系统下是否还有任务。
2. 如果系统下仍存在任务，则不允许删除。

## 任务维护

### 1. 查询任务列表

- `GET /api/manage/tasks`

### 2. 查询任务详情

- `GET /api/manage/tasks/{taskCode}`

说明：

1. 返回任务主数据
2. 返回任务依赖关系列表

### 3. 新增任务

- `POST /api/manage/tasks`

### 4. 修改任务

- `PUT /api/manage/tasks/{taskCode}`

### 5. 删除任务

- `DELETE /api/manage/tasks/{taskCode}`

说明：

1. 删除任务时会同时删除该任务的依赖关系
2. 新增或修改任务时，会一并重建依赖关系

## 任务保存请求结构

任务保存请求除任务本身字段外，还支持同时提交依赖关系列表：

```json
{
  "taskCode": "ACPL_BAT_01",
  "taskName": "核算平台日终批处理任务",
  "systemCode": "ACPL",
  "ownerName": "王臣",
  "supervisorName": "谢卫华",
  "preRequisiteProd": "核心日终文件",
  "theBatchProd": "核算平台批处理结果文件",
  "planStartTime": "2026-03-31 21:30:00",
  "planEndTime": "2026-03-31 22:05:00",
  "defaultCostMinutes": 35,
  "avgCostMinutes": 32,
  "delayMinutes": 10,
  "frequency": "D",
  "displayFlag": "1",
  "posX": 530,
  "posY": 340,
  "status": "1",
  "remark": "核算主批任务",
  "dependencies": [
    {
      "preTaskCode": "BDPS_BAT_01",
      "direction": "RIGHT"
    }
  ]
}
```
