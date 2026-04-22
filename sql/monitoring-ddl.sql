DROP TABLE IF EXISTS monitor_task_log;

DROP TABLE IF EXISTS monitor_task_instance;

DROP TABLE IF EXISTS monitor_task_dependency;

DROP TABLE IF EXISTS monitor_task;

DROP TABLE IF EXISTS monitor_system;

CREATE TABLE monitor_system (
    system_code VARCHAR(64) NOT NULL COMMENT '系统编码',
    system_name VARCHAR(128) NOT NULL COMMENT '系统名称',
    owner_name VARCHAR(64) COMMENT '系统负责人',
    supervisor_name VARCHAR(64) COMMENT '监督人',
    is_flag VARCHAR(1) NOT NULL DEFAULT '0' COMMENT '是否启用状态，0启用，1停用',
    remark VARCHAR(512) COMMENT '备注',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (system_code)
) COMMENT='监控系统表';

CREATE TABLE monitor_task (
    task_code VARCHAR(64) NOT NULL COMMENT '任务编号',
    task_name VARCHAR(128) NOT NULL COMMENT '批处理任务',
    system_code VARCHAR(64) NOT NULL COMMENT '所属系统编码',
    pre_requisite_prod VARCHAR(512) COMMENT '前置依赖产物',
    the_batch_prod VARCHAR(512) COMMENT '本批处理任务产出物',
    plan_start_time TIME NOT NULL COMMENT '跑批预计开始时间，格式 HH:mm:ss',
    plan_end_time TIME NOT NULL COMMENT '跑批预计结束时间，格式 HH:mm:ss',
    default_cost_minutes INT DEFAULT 0 COMMENT '预计耗时(单位分钟)',
    batch_processing VARCHAR(10) COMMENT '跑批周期，D:每日 M:月末 NM:非月末',
    is_path VARCHAR(1) NOT NULL DEFAULT '0' COMMENT '是否关键路劲，0是，1否',
    is_flag VARCHAR(1) NOT NULL DEFAULT '0' COMMENT '是否大屏展示，0是，1否',
    pos_x INT NOT NULL DEFAULT 0 COMMENT '横坐标X',
    pos_y INT NOT NULL DEFAULT 0 COMMENT '纵坐标Y',
    remark VARCHAR(200) COMMENT '备注',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (task_code)
) COMMENT='监控任务表';

CREATE TABLE monitor_task_dependency (
    id VARCHAR(64) COMMENT '主键ID',
    pre_task_code VARCHAR(64) NOT NULL COMMENT '依赖任务编号',
    task_code VARCHAR(64) NOT NULL COMMENT '任务编号',
    direction VARCHAR(64) COMMENT '节点方向',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (pre_task_code,task_code)
) COMMENT='任务依赖表';

CREATE TABLE monitor_task_instance (
    id VARCHAR(64) COMMENT '主键ID',
    biz_date VARCHAR(64) NOT NULL COMMENT '业务日期 YYYYMMDD',
    run_times INT NOT NULL DEFAULT 0 COMMENT '重跑次数',
    task_code VARCHAR(64) NOT NULL COMMENT '任务编号',
    system_code VARCHAR(64) NOT NULL COMMENT '所属系统编码',
    plan_start_time TIME NOT NULL COMMENT '计划开始时间，格式 HH:mm:ss',
    plan_end_time TIME COMMENT '计划结束时间，格式 HH:mm:ss',
    actual_start_time TIME COMMENT '实际开始时间，格式 HH:mm:ss',
    actual_end_time TIME COMMENT '实际结束时间，格式 HH:mm:ss',
    latest_start_time TIME COMMENT '最晚开始时间，格式 HH:mm:ss',
    latest_end_time TIME COMMENT '最晚结束时间，格式 HH:mm:ss',
    current_cost_minutes INT DEFAULT 0 COMMENT '当前耗时，单位分钟',
    avg_cost_minutes INT DEFAULT 0 COMMENT '历史平均耗时(单位分钟)',
    delayed_flag INT NOT NULL DEFAULT 0 COMMENT '是否延迟，1是，0否',
    timeout_flag INT NOT NULL DEFAULT 0 COMMENT '是否超时，1是，0否',
    result_status VARCHAR(32) COMMENT '执行结果状态',
    is_flag VARCHAR(1) NOT NULL DEFAULT '0' COMMENT '是否展示，1否，0是',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT='任务实例表';

CREATE TABLE monitor_task_log (
    id VARCHAR(64) NOT NULL COMMENT '主键ID',
    request_url VARCHAR(200) NOT NULL COMMENT '调用URL',
    request_status VARCHAR(32) NOT NULL COMMENT '动作状态 start/end/restart/fail',
    system_code VARCHAR(64) NOT NULL COMMENT '所属系统编码',
    task_code VARCHAR(64) NOT NULL COMMENT '任务编码',
    biz_date VARCHAR(64) NOT NULL COMMENT '业务日期 YYYYMMDD',
    run_times INT NOT NULL DEFAULT 0 COMMENT '重跑次数',
    request_json TEXT COMMENT '请求报文',
    result_status VARCHAR(32) COMMENT '执行结果状态',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) COMMENT='任务调用日志表';
