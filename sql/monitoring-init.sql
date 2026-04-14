-- =========================================================
-- 任务监控平台初始化示例数据
-- 说明：
-- 1. 先执行 monitoring-ddl.sql
-- 2. 再执行本文件
-- 3. 示例业务日期使用 20260407
-- =========================================================

delete from monitor_task_log;
delete from monitor_task_instance;
delete from monitor_task_dependency;
delete from monitor_task;
delete from monitor_system;

insert into monitor_system
(system_code, system_name, owner_name, supervisor_name, is_flag, remark)
values
    ('ACPL', '核算平台', '王臣', '谢卫华', '0', '核心核算相关任务'),
    ('BDPS', '大数据平台', '刘崇皇', '吴鹏飞', '0', '负责装数和抽数'),
    ('NWLS', '新网贷系统', '温强', '何锋', '0', '依赖核算平台卸数'),
    ('RICP', '老网贷系统', '张毅', '何锋', '0', '包含核算与平台批处理'),
    ('WEDS', '小微信贷管理系统', '赵永刚', '陈榕', '0', '小微信贷日终'),
    ('HOST', '核心', '王臣', '刘日旭', '0', '核心日常批处理'),
    ('WARE', '中间业务平台', '黄海明', '曾星', '0', '受托支付等中间业务');

insert into monitor_task
(task_code, task_name, system_code, pre_requisite_prod, the_batch_prod,
 plan_start_time, plan_end_time, default_cost_minutes, batch_processing, is_path,
 pos_x, pos_y, is_flag, remark)
values
    ('RICP_BAT_01', '老网贷核算日常批处理任务', 'RICP', '', '老网贷核算结果',
     '20:00:00', '20:10:00', 10, 'D', '0', 100, 80, '0', '老网贷核算任务'),
    ('ACPL_BAT_01', '核算平台日终批处理任务', 'ACPL', '老网贷核算结果', '核算平台结果文件',
     '21:30:00', '22:05:00', 35, 'D', '0', 300, 80, '0', '核心主任务'),
    ('NWLS_BAT_01', '新网贷系统卸数任务', 'NWLS', '核算平台结果文件', '新网贷卸数结果',
     '21:30:00', '22:50:00', 80, 'D', '0', 500, 80, '0', '新网贷卸数任务'),
    ('WEDS_BAT_01', '小微贷日终', 'WEDS', '', '小微贷日终数据',
     '20:00:00', '23:00:00', 180, 'D', '0', 700, 80, '0', '小微贷日终任务'),
    ('WARE_BAT_01', '中间业务平台受托支付对账', 'WARE', '核算平台结果文件', '受托支付对账结果',
     '21:00:00', '21:30:00', 30, 'D', '0', 900, 80, '0', '受托支付对账'),
    ('HOST_BAT_03', '核心日常批处理任务', 'HOST', '受托支付对账结果', '核心批处理结果',
     '21:00:00', '21:20:00', 20, 'D', '0', 1100, 80, '0', '核心日常任务');

insert into monitor_task_dependency
(id, pre_task_code, task_code, direction)
values
    ('DEP001', 'RICP_BAT_01', 'ACPL_BAT_01', 'RIGHT'),
    ('DEP002', 'ACPL_BAT_01', 'NWLS_BAT_01', 'RIGHT'),
    ('DEP003', 'ACPL_BAT_01', 'WARE_BAT_01', 'BOTTOM'),
    ('DEP004', 'WARE_BAT_01', 'HOST_BAT_03', 'RIGHT');

insert into monitor_task_instance
(id, biz_date, run_times, task_code, system_code, plan_start_time, latest_start_time, plan_end_time,
 actual_start_time, actual_end_time, latest_end_time, current_cost_minutes, avg_cost_minutes, delayed_flag, timeout_flag,
 result_status, is_flag, created_time, updated_time)
values
    ('INS001', '20260407', 0, 'RICP_BAT_01', 'RICP', '20:00:00', '20:10:00', '20:10:00',
     '20:00:03', '20:08:30', '20:18:30', 9, 10, 0, 0, 'SUCCESS', '0', now(), now()),
    ('INS002', '20260407', 0, 'ACPL_BAT_01', 'ACPL', '21:30:00', '21:40:00', '22:05:00',
     '21:31:20', '21:49:58', '22:24:58', 19, 35, 0, 0, 'FAILED', '0', now(), now()),
    ('INS003', '20260407', 0, 'NWLS_BAT_01', 'NWLS', '21:30:00', '21:40:00', '22:50:00',
     '21:36:00', null, '22:56:00', 54, 80, 0, 0, 'RUNNING', '0', now(), now()),
    ('INS004', '20260407', 0, 'WEDS_BAT_01', 'WEDS', '20:00:00', '20:10:00', '23:00:00',
     null, null, '23:00:00', 0, 180, 1, 0, 'DELAYED', '0', now(), now()),
    ('INS005', '20260407', 0, 'WARE_BAT_01', 'WARE', '21:00:00', '21:10:00', '21:30:00',
     '21:00:12', '21:18:45', '21:48:45', 19, 30, 0, 0, 'SUCCESS', '0', now(), now()),
    ('INS006', '20260407', 0, 'HOST_BAT_03', 'HOST', '21:00:00', '21:10:00', '21:20:00',
     null, null, '21:20:00', 0, 20, 0, 0, 'NOTSTART', '0', now(), now());

insert into monitor_task_log
(id, request_id, request_url, request_status, system_code, task_code, biz_date, run_times, request_json, result_status)
values
    ('LOG001', 'REQ202604070001', '/api/monitor/task/report', 'start', 'RICP', 'RICP_BAT_01', '20260407', 0,
     '{"systemCode":"RICP","taskCode":"RICP_BAT_01","bizDate":"2026-04-07","status":"start"}', 'RUNNING'),
    ('LOG002', 'REQ202604070002', '/api/monitor/task/report', 'stop', 'RICP', 'RICP_BAT_01', '20260407', 0,
     '{"systemCode":"RICP","taskCode":"RICP_BAT_01","bizDate":"2026-04-07","status":"stop"}', 'SUCCESS'),
    ('LOG003', 'REQ202604070003', '/api/monitor/task/report', 'start', 'ACPL', 'ACPL_BAT_01', '20260407', 0,
     '{"systemCode":"ACPL","taskCode":"ACPL_BAT_01","bizDate":"2026-04-07","status":"start"}', 'RUNNING'),
    ('LOG004', 'REQ202604070004', '/api/monitor/task/report', 'fail', 'ACPL', 'ACPL_BAT_01', '20260407', 0,
     '{"systemCode":"ACPL","taskCode":"ACPL_BAT_01","bizDate":"2026-04-07","status":"fail"}', 'FAILED'),
    ('LOG005', 'REQ202604070005', '/api/monitor/task/report', 'start', 'NWLS', 'NWLS_BAT_01', '20260407', 0,
     '{"systemCode":"NWLS","taskCode":"NWLS_BAT_01","bizDate":"2026-04-07","status":"start"}', 'RUNNING');
