package com.example.lotterysystem.service.dto;

import com.example.lotterysystem.service.enums.ActivityPrizeTiersStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class WinningRecordDTO {
    /**
     * 中奖者id
     */
    private Long winnerId;

    /**
     * 中奖者姓名
     */
    private String winnerName;

    /**
     * 奖品名
     */
    private String prizeName;

    /**
     * 等级
     */
    private ActivityPrizeTiersStatusEnum prizeTier;

    /**
     * 中奖时间
     */
    private Date winningTime;
}
