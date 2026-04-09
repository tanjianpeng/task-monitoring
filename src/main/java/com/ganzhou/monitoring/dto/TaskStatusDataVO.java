package com.ganzhou.monitoring.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Description: 本地大屏任务状态接口返回对象。 将任务节点列表和依赖关系列表拆开返回，便于前端分别渲染节点和箭头。
 *
 * @author tanjianpeng
 * @time 2026-04-07 18:14:49
 * @version 1.0
 */
@Data
@ApiModel(value = "本地大屏任务状态返回对象")
public class TaskStatusDataVO {

    /**
     * 任务节点列表。
     */
    @ApiModelProperty(name = "taskList", value = "任务节点列表")
    private List<TaskDashboardItemVO> taskList;

    /**
     * 依赖关系节点列表。
     */
    @JsonProperty("依赖关系节点list")
    @ApiModelProperty(name = "dependencyNodeList", value = "依赖关系节点列表")
    private List<LinkVO> dependencyNodeList;
}
