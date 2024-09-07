package com.example.lotterysystem.service;

import com.example.lotterysystem.controller.param.CreateActivityParam;
import com.example.lotterysystem.service.dto.CreateActivityDTO;


public interface ActivityService {

    /**
     * 创建活动
     * @param param
     * @return
     */
    CreateActivityDTO createActivity(CreateActivityParam param);
}
