package com.ganzhou.monitoring.mapper;

import com.ganzhou.monitoring.dto.LinkVO;
import com.ganzhou.monitoring.dto.SummaryVO;
import com.ganzhou.monitoring.dto.TaskCardVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MonitorDashboardMapper {

    /**
     * 查询首页汇总统计。
     */
    SummaryVO selectSummary(@Param("bizDate") String bizDate);

    /**
     * 查询任务卡片列表。
     */
    List<TaskCardVO> selectTaskCards(@Param("bizDate") String bizDate,
                                     @Param("pageCode") String pageCode);

    /**
     * 查询单个任务卡片详情。
     */
    TaskCardVO selectTaskCard(@Param("bizDate") String bizDate,
                              @Param("taskCode") String taskCode);

    /**
     * 查询任务依赖连线。
     */
    List<LinkVO> selectLinks(@Param("pageCode") String pageCode);
}
