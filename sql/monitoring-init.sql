-- =========================================================
-- 跑批监控平台初始化示例数据
-- 说明：
-- 1. 先执行 monitoring-ddl.sql
-- 2. 再执行本文件
-- 3. 示例业务日期使用 2026-03-30
-- =========================================================

delete from monitor_task_event;
delete from monitor_task_stat_daily;
delete from monitor_task_instance;
delete from monitor_task_dependency;
delete from monitor_task_def;
delete from monitor_system;

insert into monitor_system
(system_code, system_name, owner_name, supervisor_name, system_type, status, remark)
values
    ('ACPL', '核算平台', '王臣', '谢卫华', 'CORE', '1', '核心核算相关任务'),
    ('BDPS', '大数据平台', '刘崇皇', '吴鹏飞', 'DATA', '1', '负责装数和抽数'),
    ('NWLS', '新网贷系统', '温强', '何锋', 'LOAN', '1', '依赖核算平台卸数'),
    ('RICP', '老网贷系统', '张毅', '何锋', 'LOAN', '1', '包含核算与平台批处理'),
    ('WEDS', '小微信贷管理系统', '赵永刚', '陈榕', 'LOAN', '1', '小微信贷日终'),
    ('HOST', '核心', '王臣', '刘日旭', 'CORE', '1', '核心日常批处理'),
    ('WARE', '中间业务平台', '黄海明', '曾星', 'MIDDLE', '1', '受托支付等中间业务');

insert into monitor_task_def
(task_code, task_name, system_code, owner_name, supervisor_name, plan_start_time, plan_end_time,
 default_cost_seconds, delay_tolerance_minutes, month_end_flag, auto_create_instance_flag, display_flag,
 page_code, group_code, status, remark)
values
    ('ACPL_BAT_01', '核算平台日终批处理任务', 'ACPL', '王臣', '谢卫华', '21:30:00', '22:05:00', 2100, 10, '0', '1', '1', 'SYSTEM_STATUS', 'CENTER', '1', '核算主批任务'),
    ('ACPL_BAT_02', '核算平台卸数任务', 'ACPL', '王臣', '谢卫华', '22:05:00', '22:25:00', 1200, 10, '0', '1', '1', 'SYSTEM_STATUS', 'RIGHT', '1', '核算卸数供其他系统使用'),
    ('BDPS_BAT_01', '大数据平台装数任务', 'BDPS', '刘崇皇', '吴鹏飞', '20:00:00', '20:30:00', 1800, 10, '0', '1', '1', 'SYSTEM_STATUS', 'RIGHT', '1', '大数据装数'),
    ('NWLS_BAT_01', '新网贷系统卸数任务', 'NWLS', '温强', '何锋', '21:30:00', '22:50:00', 4800, 10, '0', '1', '1', 'SYSTEM_STATUS', 'CENTER', '1', '依赖核算平台卸数'),
    ('RICP_BAT_01', '老网贷核算日常批处理任务', 'RICP', '张毅', '何锋', '20:00:00', '20:10:00', 600, 10, '0', '1', '1', 'SYSTEM_STATUS', 'LEFT', '1', '老网贷核算'),
    ('WEDS_BAT_01', '小微贷日终', 'WEDS', '赵永刚', '陈榕', '20:00:00', '23:00:00', 10800, 10, '0', '1', '1', 'SYSTEM_STATUS', 'RIGHT', '1', '小微信贷日终'),
    ('HOST_BAT_03', '核心日常批处理任务', 'HOST', '王臣', '刘日旭', '21:00:00', '21:20:00', 1200, 10, '0', '1', '1', 'SYSTEM_STATUS', 'LEFT', '1', '核心日常任务'),
    ('WARE_BAT_01', '中间业务平台受托支付对账', 'WARE', '黄海明', '曾星', '21:00:00', '21:30:00', 1800, 10, '0', '1', '1', 'SYSTEM_STATUS', 'LEFT', '1', '受托支付对账');

insert into monitor_task_dependency
(id, task_code, pre_system_code, pre_task_code, pre_task_name, dependency_type, strong_dependency_flag, dependency_desc, output_desc)
values
    (1, 'ACPL_BAT_01', 'BDPS', 'BDPS_BAT_01', '大数据平台装数任务', 'TASK', '0', '装数完成后可提供更多数据支撑', '模型数据'),
    (2, 'ACPL_BAT_02', 'ACPL', 'ACPL_BAT_01', '核算平台日终批处理任务', 'TASK', '1', '核算日终完成后才可卸数', '核算平台卸数文件'),
    (3, 'NWLS_BAT_01', 'ACPL', 'ACPL_BAT_02', '核算平台卸数任务', 'FILE', '1', '轮询核算平台卸数结果', '核算日终卸数文件8个'),
    (4, 'WARE_BAT_01', 'ACPL', 'ACPL_BAT_02', '核算平台卸数任务', 'TASK', '1', '依赖核算批后卸数', '受托支付明细'),
    (5, 'HOST_BAT_03', 'WARE', 'WARE_BAT_01', '中间业务平台受托支付对账', 'TASK', '0', '对账结果可辅助核心批处理', '受托支付对账结果');

insert into monitor_task_instance
(id, biz_date, run_no, task_code, task_name, system_code, current_status, plan_start_time, latest_start_time,
 plan_end_time, actual_start_time, actual_end_time, current_cost_seconds, avg_cost_seconds, predict_end_time,
 delayed_flag, timeout_flag, result_status, error_code, error_message, source_system_code, last_report_time, ext_json)
values
    (1, '2026-03-30', 1, 'RICP_BAT_01', '老网贷核算日常批处理任务', 'RICP', 'SUCCESS', '2026-03-30 20:00:00', '2026-03-30 20:10:00',
     '2026-03-30 20:10:00', '2026-03-30 20:00:03', '2026-03-30 20:08:30', 507, 600, '2026-03-30 20:10:03',
     '0', '0', 'SUCCESS', null, null, 'RICP', '2026-03-30 20:08:30', '{"source":"batch"}'),
    (2, '2026-03-30', 1, 'ACPL_BAT_01', '核算平台日终批处理任务', 'ACPL', 'FAILED', '2026-03-30 21:30:00', '2026-03-30 21:40:00',
     '2026-03-30 22:05:00', '2026-03-30 21:31:20', '2026-03-30 21:49:58', 1118, 2100, '2026-03-30 22:06:20',
     '0', '0', 'FAILED', 'ACPL-5001', '手动生成凭证失败', 'ACPL', '2026-03-30 21:49:58', '{"node":"acpl-node-01"}'),
    (3, '2026-03-30', 1, 'BDPS_BAT_01', '大数据平台装数任务', 'BDPS', 'SUCCESS', '2026-03-30 20:00:00', '2026-03-30 20:10:00',
     '2026-03-30 20:30:00', '2026-03-30 20:00:00', '2026-03-30 20:27:18', 1638, 1800, '2026-03-30 20:30:00',
     '0', '0', 'SUCCESS', null, null, 'BDPS', '2026-03-30 20:27:18', '{"source":"etl"}'),
    (4, '2026-03-30', 1, 'NWLS_BAT_01', '新网贷系统卸数任务', 'NWLS', 'RUNNING', '2026-03-30 21:30:00', '2026-03-30 21:40:00',
     '2026-03-30 22:50:00', '2026-03-30 21:36:00', null, 3260, 4800, '2026-03-30 22:56:00',
     '0', '0', 'RUNNING', null, null, 'NWLS', '2026-03-30 22:30:20', '{"progress":68}'),
    (5, '2026-03-30', 1, 'ACPL_BAT_02', '核算平台卸数任务', 'ACPL', 'PENDING', '2026-03-30 22:05:00', '2026-03-30 22:15:00',
     '2026-03-30 22:25:00', null, null, 0, 1200, '2026-03-30 22:25:00',
     '0', '0', null, null, null, null, null, null),
    (6, '2026-03-30', 1, 'WARE_BAT_01', '中间业务平台受托支付对账', 'WARE', 'SUCCESS', '2026-03-30 21:00:00', '2026-03-30 21:10:00',
     '2026-03-30 21:30:00', '2026-03-30 21:00:12', '2026-03-30 21:18:45', 1113, 1800, '2026-03-30 21:30:12',
     '0', '0', 'SUCCESS', null, null, 'WARE', '2026-03-30 21:18:45', '{"source":"ware-job"}'),
    (7, '2026-03-30', 1, 'HOST_BAT_03', '核心日常批处理任务', 'HOST', 'SUCCESS', '2026-03-30 21:00:00', '2026-03-30 21:10:00',
     '2026-03-30 21:20:00', '2026-03-30 21:00:05', '2026-03-30 21:16:48', 1003, 1200, '2026-03-30 21:20:05',
     '0', '0', 'SUCCESS', null, null, 'HOST', '2026-03-30 21:16:48', '{"mode":"manual"}'),
    (8, '2026-03-30', 1, 'WEDS_BAT_01', '小微贷日终', 'WEDS', 'DELAYED', '2026-03-30 20:00:00', '2026-03-30 20:10:00',
     '2026-03-30 23:00:00', null, null, 0, 10800, '2026-03-30 23:00:00',
     '1', '0', null, null, null, null, '2026-03-30 22:30:00', '{"remark":"等待前置抽数"}');

insert into monitor_task_event
(id, request_id, biz_date, run_no, task_code, system_code, event_type, event_time, start_time, end_time,
 result_status, cost_seconds, error_code, error_message, request_json, process_result, process_msg)
values
    (1, 'REQ202603300001', '2026-03-30', 1, 'RICP_BAT_01', 'RICP', 'START', '2026-03-30 20:00:03', '2026-03-30 20:00:03', null,
     'RUNNING', null, null, null, '{"taskCode":"RICP_BAT_01","eventType":"START"}', 'SUCCESS', '处理成功'),
    (2, 'REQ202603300002', '2026-03-30', 1, 'RICP_BAT_01', 'RICP', 'FINISH', '2026-03-30 20:08:30', '2026-03-30 20:00:03', '2026-03-30 20:08:30',
     'SUCCESS', 507, null, null, '{"taskCode":"RICP_BAT_01","eventType":"FINISH"}', 'SUCCESS', '处理成功'),
    (3, 'REQ202603300003', '2026-03-30', 1, 'ACPL_BAT_01', 'ACPL', 'START', '2026-03-30 21:31:20', '2026-03-30 21:31:20', null,
     'RUNNING', null, null, null, '{"taskCode":"ACPL_BAT_01","eventType":"START"}', 'SUCCESS', '处理成功'),
    (4, 'REQ202603300004', '2026-03-30', 1, 'ACPL_BAT_01', 'ACPL', 'FAIL', '2026-03-30 21:49:58', '2026-03-30 21:31:20', '2026-03-30 21:49:58',
     'FAILED', 1118, 'ACPL-5001', '手动生成凭证失败', '{"taskCode":"ACPL_BAT_01","eventType":"FAIL"}', 'SUCCESS', '处理成功'),
    (5, 'REQ202603300005', '2026-03-30', 1, 'NWLS_BAT_01', 'NWLS', 'START', '2026-03-30 21:36:00', '2026-03-30 21:36:00', null,
     'RUNNING', null, null, null, '{"taskCode":"NWLS_BAT_01","eventType":"START"}', 'SUCCESS', '处理成功'),
    (6, 'REQ202603300006', '2026-03-30', 1, 'NWLS_BAT_01', 'NWLS', 'HEARTBEAT', '2026-03-30 22:30:20', '2026-03-30 21:36:00', null,
     'RUNNING', 3260, null, null, '{"taskCode":"NWLS_BAT_01","eventType":"HEARTBEAT","progress":68}', 'SUCCESS', '处理成功');

insert into monitor_task_stat_daily
(id, biz_date, task_code, system_code, run_no, success_flag, delayed_flag, timeout_flag, month_end_flag, cost_seconds)
values
    (1, '2026-03-29', 'RICP_BAT_01', 'RICP', 1, '1', '0', '0', '0', 530),
    (2, '2026-03-29', 'ACPL_BAT_01', 'ACPL', 1, '1', '0', '0', '0', 2050),
    (3, '2026-03-29', 'BDPS_BAT_01', 'BDPS', 1, '1', '0', '0', '0', 1780),
    (4, '2026-03-29', 'NWLS_BAT_01', 'NWLS', 1, '1', '0', '0', '0', 4720),
    (5, '2026-03-30', 'RICP_BAT_01', 'RICP', 1, '1', '0', '0', '0', 507),
    (6, '2026-03-30', 'ACPL_BAT_01', 'ACPL', 1, '0', '0', '0', '0', 1118),
    (7, '2026-03-30', 'BDPS_BAT_01', 'BDPS', 1, '1', '0', '0', '0', 1638),
    (8, '2026-03-30', 'WEDS_BAT_01', 'WEDS', 1, '0', '1', '0', '0', 0);
