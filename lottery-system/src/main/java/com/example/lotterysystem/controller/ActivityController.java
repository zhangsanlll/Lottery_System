package com.example.lotterysystem.controller;

import com.example.lotterysystem.common.errorcode.ControllerErrorCodeConstants;
import com.example.lotterysystem.common.exception.ControllerException;
import com.example.lotterysystem.common.pojo.CommonResult;
import com.example.lotterysystem.common.utils.JacksonUtil;
import com.example.lotterysystem.controller.param.CreateActivityParam;
import com.example.lotterysystem.controller.result.CreateActivityResult;
import com.example.lotterysystem.service.ActivityService;
import com.example.lotterysystem.service.dto.CreateActivityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivityController {

    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;


    @RequestMapping("/activity/create")
    public CommonResult<CreateActivityResult> createActivity(
            @RequestBody @Validated CreateActivityParam param){
        logger.info("createActivity createActivityParam:{}", JacksonUtil.writeValueAsString(param));
        CreateActivityDTO createActivityDTO = activityService.createActivity(param);
        return CommonResult.success(convertToCreateActivityResult(createActivityDTO));
    }

    private CreateActivityResult convertToCreateActivityResult(CreateActivityDTO createActivityDTO) {
        if(null == createActivityDTO){
            throw new ControllerException(ControllerErrorCodeConstants.CREATE_ACTIVITY_ERROR);
        }
        CreateActivityResult result = new CreateActivityResult();
        result.setActivityId(createActivityDTO.getActivityId());
        return result;
    }
}
