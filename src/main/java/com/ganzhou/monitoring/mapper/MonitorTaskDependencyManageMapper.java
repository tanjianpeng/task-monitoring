package com.ganzhou.monitoring.mapper;

import java.util.List;

import com.ganzhou.monitoring.entity.MonitorTaskDependency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description: 任务依赖关系管理 Mapper。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Mapper
public interface MonitorTaskDependencyManageMapper {

    /**
     * 按任务编码查询其全部依赖关系。
     *
     * @param taskCode 当前任务编码，表示要查看哪个任务依赖了哪些前置任务
     * @return 依赖关系列表
     */
    List<MonitorTaskDependency> selectByTaskCode(@Param("taskCode") String taskCode);

    /**
     * 按任务编码删除依赖关系。
     *
     * @param taskCode 当前任务编码，表示删除该任务对应的全部依赖配置
     * @return 影响行数
     */
    int deleteByTaskCode(@Param("taskCode") String taskCode);

    /**
     * 按任务编码删除与该任务相关的全部依赖关系。
     * 既删除“当前任务依赖别人”的记录，也删除“别人依赖当前任务”的记录。
     *
     * @param taskCode 任务编码
     * @return 影响行数
     */
    int deleteRelatedByTaskCode(@Param("taskCode") String taskCode);

    /**
     * 批量插入任务依赖关系。
     *
     * @param list 依赖关系列表，每条数据表示一个前置任务与当前任务的关系
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<MonitorTaskDependency> list);
}
