package com.ganzhou.monitoring.controller;

import com.ganzhou.monitoring.common.ApiResponse;
import com.ganzhou.monitoring.dto.SystemSaveRequest;
import com.ganzhou.monitoring.entity.MonitorSystem;
import com.ganzhou.monitoring.service.SystemManageService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统主数据管理接口。
 * 用于页面维护监控系统信息。
 */
@RestController
@RequestMapping("/api/manage/systems")
@RequiredArgsConstructor
public class SystemManageController {

    private final SystemManageService systemManageService;

    /** 查询系统列表。 */
    @GetMapping
    public ApiResponse<List<MonitorSystem>> list() {
        return ApiResponse.success(systemManageService.listSystems());
    }

    /** 查询系统详情。 */
    @GetMapping("/{systemCode}")
    public ApiResponse<MonitorSystem> detail(@PathVariable String systemCode) {
        return ApiResponse.success(systemManageService.getSystem(systemCode));
    }

    /** 新增系统。 */
    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody SystemSaveRequest request) {
        systemManageService.createSystem(request);
        return ApiResponse.successMessage("系统新增成功");
    }

    /** 修改系统。 */
    @PutMapping("/{systemCode}")
    public ApiResponse<Void> update(@PathVariable String systemCode,
                                    @Valid @RequestBody SystemSaveRequest request) {
        request.setSystemCode(systemCode);
        systemManageService.updateSystem(request);
        return ApiResponse.successMessage("系统修改成功");
    }

    /** 删除系统。 */
    @DeleteMapping("/{systemCode}")
    public ApiResponse<Void> delete(@PathVariable String systemCode) {
        systemManageService.deleteSystem(systemCode);
        return ApiResponse.successMessage("系统删除成功");
    }
}
