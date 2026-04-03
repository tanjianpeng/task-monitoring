package com.ganzhou.monitoring.mapper;

import com.ganzhou.monitoring.entity.MonitorSystem;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统主数据管理 Mapper。
 */
@Mapper
public interface MonitorSystemManageMapper {

    List<MonitorSystem> selectAll();

    MonitorSystem selectByCode(@Param("systemCode") String systemCode);

    int insert(MonitorSystem system);

    int updateByCode(MonitorSystem system);

    int deleteByCode(@Param("systemCode") String systemCode);
}
