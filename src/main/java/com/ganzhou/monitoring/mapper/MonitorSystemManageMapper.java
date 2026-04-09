package com.ganzhou.monitoring.mapper;

import java.util.List;

import com.ganzhou.monitoring.entity.MonitorSystem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description: 系统主数据管理 Mapper。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Mapper
public interface MonitorSystemManageMapper {

    /**
     * 查询全部系统主数据。
     *
     * @return 系统列表，供管理页和本地大屏查询使用
     */
    List<MonitorSystem> selectAll();

    /**
     * 按系统编码查询单个系统信息。
     *
     * @param systemCode 系统编码，用于唯一定位一条系统记录
     * @return 系统信息，不存在时返回 null
     */
    MonitorSystem selectByCode(@Param("systemCode") String systemCode);

    /**
     * 新增系统主数据。
     *
     * @param system 系统实体，包含系统名称、负责人、监督人等信息
     * @return 影响行数
     */
    int insert(MonitorSystem system);

    /**
     * 按系统编码更新系统主数据。
     *
     * @param system 系统实体，systemCode 表示要更新哪条记录，其他字段表示最新内容
     * @return 影响行数
     */
    int updateByCode(MonitorSystem system);

    /**
     * 按系统编码删除系统记录。
     *
     * @param systemCode 系统编码，表示要删除的系统
     * @return 影响行数
     */
    int deleteByCode(@Param("systemCode") String systemCode);
}
