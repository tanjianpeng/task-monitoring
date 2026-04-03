package com.ganzhou.monitoring.service;

import com.ganzhou.monitoring.dto.LinkVO;
import com.ganzhou.monitoring.dto.SummaryVO;
import com.ganzhou.monitoring.dto.TaskCardVO;
import com.ganzhou.monitoring.dto.TaskDetailVO;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

    /**
     * 查询汇总统计。
     */
    SummaryVO getSummary(LocalDate bizDate);

    /**
     * 查询大屏任务卡片列表。
     */
    List<TaskCardVO> getTaskCards(LocalDate bizDate, String pageCode);

    /**
     * 查询任务依赖连线。
     */
    List<LinkVO> getLinks(String pageCode);

    /**
     * 查询任务详情。
     */
    TaskDetailVO getTaskDetail(LocalDate bizDate, String taskCode);
}
