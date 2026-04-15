package com.ganzhou.monitoring.mapper;

import java.util.List;

import com.ganzhou.monitoring.dto.TaskEventVO;
import com.ganzhou.monitoring.entity.MonitorTaskEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description: 任务调用日志 Mapper。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Mapper
public interface MonitorTaskEventMapper {

    /**
     * 插入任务调用日志。
     *
     * @param event 日志实体，记录外部系统本次 start/end/restart/fail 调用内容
     * @return 影响行数
     */
    int insert(MonitorTaskEvent event);

    /**
     * 按业务日期和任务编码查询任务调用日志。
     *
     * @param bizDate 业务日期，格式 yyyyMMdd，表示查看哪一天的调用记录
     * @param taskCode 任务编码，表示查看哪个任务的调用记录
     * @return 事件流水列表，通常用于任务详情或排障查看
     */
    List<TaskEventVO> selectByBizDateAndTaskCode(@Param("bizDate") String bizDate,
                                                 @Param("taskCode") String taskCode);
}
