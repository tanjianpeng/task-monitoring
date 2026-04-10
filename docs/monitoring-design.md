# 数据大屏跑批监控设计方案

## 1. 需求目标

本方案用于支撑数据大屏展示不同系统下不同跑批任务的运行状态，满足以下目标：

1. 各业务系统通过统一接口上报任务开始、成功结束、失败结束、重跑开始等状态。
2. 平台将上报数据落库，形成系统主数据、任务主数据、任务实例、任务调用日志、依赖关系等数据。
3. 大屏可实时查询某一日任务运行情况，并按未开始、延迟、执行中、成功、失败等状态展示。
4. 支持甲方设计图中的字段，如所属系统、负责人、监督人、预计开始时间、最晚开始时间、历史平均耗时、当前耗时、预计结束时间、开始时间、结束时间等。
5. 支持后续扩展更多系统、更多任务，而不需要反复改表。

## 2. 设计原则

1. 主数据和运行数据分离。
2. 任务定义和任务实例分离。
3. 状态以“当前快照 + 调用日志”双模型保存。
4. 展示字段和业务数据统一维护。
5. 接口统一，调用系统只需要按任务编码上报。

## 3. 核心业务模型

### 3.1 主体对象

1. 系统
2. 任务
3. 任务依赖关系
4. 每日任务实例
5. 任务调用日志

### 3.2 主数据与运行数据关系

1. `monitor_system` 维护系统信息。
2. `monitor_task` 维护任务定义和大屏展示相关配置。
3. `monitor_task_dependency` 维护任务前置依赖和箭头方向。
4. `monitor_task_instance` 按“业务日期 + 任务 + 重跑次数”维护每日运行实例。
5. `monitor_task_log` 保存外部系统每次回调的原始请求日志。

## 4. 状态设计

### 4.1 统一状态枚举

统一采用以下五种状态：

1. `NOTSTART` 未开始
2. `DELAYED` 延迟
3. `RUNNING` 执行中
4. `SUCCESS` 成功
5. `FAILED` 失败

### 4.2 状态判定规则

1. 外部未回传开始时间时，为 `NOTSTART`。
2. 收到 `start` 或 `restart` 后，若开始时间未超过最晚开始时间，为 `RUNNING`。
3. 收到 `start` 或 `restart` 后，若开始时间超过最晚开始时间，为 `DELAYED`。
4. 收到 `stop` 后，为 `SUCCESS`。
5. 收到 `fail` 后，为 `FAILED`。
6. 已开始未结束且当前时间大于预计结束时间时，也归为 `DELAYED`。
7. 已正常结束，但实际结束时间大于预计结束时间时，也记为延迟，状态仍保持 `SUCCESS` 或 `FAILED`，同时标记延迟。

### 4.3 设计图字段映射

1. 计划开始时间：来自 `monitor_task.plan_start_time`
2. 最晚开始时间：实例表 `latest_start_time`
3. 开始时间：来自实例表 `actual_start_time`
4. 结束时间：来自实例表 `actual_end_time`
5. 当前耗时：来自实例表 `current_cost_minutes`
6. 历史平均耗时：平日视图取往前 30 个非月底业务日成功平均耗时，月底视图取往前 6 个自然月底业务日成功平均耗时
7. 预计结束时间：开始时间 + 历史平均耗时

## 5. 库表设计

### 5.1 `monitor_system` 系统表

用途：维护被监控系统基础信息。

关键字段：

1. `system_code` 系统编码
2. `system_name` 系统名称
3. `owner_name` 系统负责人
4. `supervisor_name` 监督人
5. `is_flag` 是否启用状态，0启用，1停用
6. `remark` 备注

### 5.2 `monitor_task` 任务表

用途：维护任务静态信息，是实例生成的基础。

关键字段：

1. `task_code` 任务编码
2. `task_name` 任务名称
3. `system_code` 所属系统编码
4. `pre_requisite_prod` 前置依赖产物
5. `the_batch_prod` 本批处理任务产出物
6. `plan_start_time` 计划开始时间
7. `plan_end_time` 计划结束时间
8. `default_cost_minutes` 默认耗时分钟数
9. `is_flag` 是否大屏展示，0是，1否
10. `pos_x`、`pos_y` 大屏坐标
11. `remark` 备注

### 5.3 `monitor_task_dependency` 任务依赖表

用途：维护任务依赖关系，供前端绘制箭头链路。

关键字段：

1. `pre_task_code` 前置任务编码
2. `task_code` 当前任务编码
3. `direction` 箭头方向，支持 `TOP`、`BOTTOM`、`LEFT`、`RIGHT`

### 5.4 `monitor_task_instance` 任务实例表

用途：保存每天每个任务的运行快照，是大屏展示的核心表。

关键字段：

1. `biz_date` 业务日期
2. `run_times` 重跑次数
3. `task_code` 任务编码
4. `system_code` 所属系统编码
5. `plan_start_time` 计划开始时间
6. `latest_start_time` 最晚开始时间
7. `plan_end_time` 计划结束时间
8. `actual_start_time` 实际开始时间
9. `actual_end_time` 实际结束时间
10. `latest_end_time` 最晚结束时间
11. `current_cost_minutes` 当前耗时分钟数
12. `avg_cost_minutes` 历史平均耗时分钟数
13. `batch_processing` 跑批周期，`D` 表示每日跑批，`M` 表示月末跑批，`NM` 表示非月末跑批
14. `delayed_flag` 是否延迟
15. `timeout_flag` 是否超时
16. `result_status` 执行结果状态
17. `is_flag` 是否隐藏，1否，0是

### 5.5 `monitor_task_log` 任务调用日志表

用途：保存每次接口调用的原始日志，便于审计和排错。

关键字段：

1. `request_id` 请求流水号
2. `request_url` 调用地址
3. `request_status` 请求动作状态，取值 `start`、`stop`、`restart`、`fail`
4. `system_code` 系统编码
5. `task_code` 任务编码
6. `biz_date` 业务日期
7. `run_times` 重跑次数
8. `request_json` 原始请求报文
9. `result_status` 执行结果状态

## 6. 实例生成机制

建议每日 0 点由定时器初始化当天实例：

1. 读取启用中的任务。
2. 按业务日期生成 `monitor_task_instance`。
3. 将计划开始时间、计划结束时间、最晚开始时间、默认耗时等写入实例。
4. 初始状态设置为 `NOTSTART`。

这样做的好处：

1. 大屏可以在任务尚未开始前就展示完整卡片。
2. 业务系统只需要上报状态，不需要负责创建实例。
3. 多台服务器部署时，通过数据库锁防止重复初始化。

## 7. 接口设计

### 7.1 外部系统上报接口

统一上报接口：

`POST /api/monitor/task/report`

`status` 字段说明如下：

1. `start` 表示开始
2. `stop` 表示成功结束
3. `restart` 表示重跑开始
4. `fail` 表示失败结束

请求核心字段：

1. `systemCode` 系统编码
2. `taskCode` 任务编码
3. `bizDate` 业务日期
4. `status` 本次上报状态
5. `requestTime` 请求时间
6. `runNo` 重跑次数
7. `startTime` 开始时间
8. `endTime` 结束时间
9. `costSeconds` 耗时秒数
10. `sign` 签名

### 7.2 本地大屏查询接口

1. `GET /api/monitor/local/queryStatistics`
2. `GET /api/monitor/local/querySystems`
3. `GET /api/monitor/local/queryTaskStatus`

说明：

1. `queryStatistics` 返回顶部统计字符串对象，仅包含系统数、任务总数、待执行、执行中、成功、失败。
2. `querySystems` 返回系统信息卡片 list，支持按系统名称模糊查询，并返回待执行、执行中、成功、失败及总数。
3. `queryTaskStatus` 返回任务状态和依赖关系列表。
4. 任务状态列表会按依赖关系尽量整理顺序，方便前端直接渲染。
5. 三个本地查询接口的业务日期都优先取实例表最新一条业务日期，实例表为空时回退到昨天日期。

### 7.3 管理接口

1. `GET /api/manage/systems`
2. `GET /api/manage/systems/{systemCode}`
3. `POST /api/manage/systems`
4. `PUT /api/manage/systems/{systemCode}`
5. `DELETE /api/manage/systems/{systemCode}`
6. `GET /api/manage/tasks`
7. `GET /api/manage/tasks/{taskCode}`
8. `POST /api/manage/tasks`
9. `PUT /api/manage/tasks/{taskCode}`
10. `DELETE /api/manage/tasks/{taskCode}`

## 8. 推荐处理流程

### 8.1 外部系统回调流程

1. 接收请求并校验签名。
2. 校验系统编码、任务编码是否存在。
3. 记录 `monitor_task_log` 调用日志。
4. 按 `status` 类型更新对应任务实例。
5. 必要时回写任务平均耗时。
6. 返回标准结果。

### 8.2 大屏读取流程

1. 查询顶部统计汇总。
2. 查询系统列表。
3. 查询任务实例快照及依赖关系。
4. 前端按任务坐标和依赖方向渲染任务卡片及箭头。

## 9. 关键计算口径

### 9.1 最晚开始时间

计算方式：

`最晚开始时间 = 计划开始时间 + 允许延迟分钟数`

其中允许延迟分钟数统一通过预留的运行配置查询能力按 `taskCode` 获取，未配置时默认按 0 分钟处理。

### 9.4 历史平均耗时

1. 平日场景：从当前业务日期往前取 30 个非月底业务日，统计成功实例平均耗时。
2. 月底场景：从当前业务日期往前取 6 个自然月底业务日，统计成功实例平均耗时。
3. 待执行任务没有实时耗时，悬浮弹框中的当前耗时返回空值。

### 9.2 预计结束时间

1. 已开始未结束：`实际开始时间 + 历史平均耗时`
2. 未开始：暂无预计结束时间，待收到开始时间后再计算

### 9.3 实时状态更新

1. 谁调用上报接口，就更新谁对应的任务实例。
2. 未调用前保持 `NOTSTART`。
3. `stop` 直接视为成功。
4. `fail` 直接视为失败。
5. `restart` 会累计重跑次数，并按开始时间与最晚开始时间比较后进入 `RUNNING` 或 `DELAYED`。

## 10. 风险与注意事项

1. 不同系统时间来源不一致，建议记录请求时间和服务端处理时间。
2. 一个任务可能有多条依赖关系，因此依赖表必须独立设计。
3. 大屏坐标字段由任务主数据统一维护，调整布局时需同步维护主数据。
4. 同一任务一天可能重复执行，实例表通过 `run_times` 表示重跑次数。
5. 初始化实例的定时器在多节点部署下应使用分布式锁或数据库锁防重。
