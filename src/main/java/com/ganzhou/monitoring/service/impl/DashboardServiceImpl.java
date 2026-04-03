package com.ganzhou.monitoring.service.impl;

import com.ganzhou.monitoring.dto.LinkVO;
import com.ganzhou.monitoring.dto.SummaryVO;
import com.ganzhou.monitoring.dto.TaskCardVO;
import com.ganzhou.monitoring.dto.TaskDetailVO;
import com.ganzhou.monitoring.mapper.MonitorDashboardMapper;
import com.ganzhou.monitoring.mapper.MonitorTaskEventMapper;
import com.ganzhou.monitoring.service.DashboardService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    /**
     * 大屏相关聚合查询 Mapper。
     */
    private final MonitorDashboardMapper dashboardMapper;

    /**
     * 事件流水查询 Mapper。
     */
    private final MonitorTaskEventMapper taskEventMapper;

    @Override
    public SummaryVO getSummary(LocalDate bizDate) {
        return dashboardMapper.selectSummary(formatBizDate(bizDate));
    }

    @Override
    public List<TaskCardVO> getTaskCards(LocalDate bizDate, String pageCode) {
        return dashboardMapper.selectTaskCards(formatBizDate(bizDate), pageCode);
    }

    @Override
    public List<LinkVO> getLinks(String pageCode) {
        // 连线接口用于渲染任务之间的前后依赖关系。
        return dashboardMapper.selectLinks(pageCode);
    }

    @Override
    public TaskDetailVO getTaskDetail(LocalDate bizDate, String taskCode) {
        String formatBizDate = formatBizDate(bizDate);
        TaskDetailVO detail = new TaskDetailVO();
        detail.setInstance(dashboardMapper.selectTaskCard(formatBizDate, taskCode));
        detail.setEvents(taskEventMapper.selectByBizDateAndTaskCode(formatBizDate, taskCode));
        return detail;
    }

    private String formatBizDate(LocalDate bizDate) {
        return bizDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}
