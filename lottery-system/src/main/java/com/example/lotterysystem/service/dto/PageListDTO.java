package com.example.lotterysystem.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageListDTO<T> {
    /**
     * 总量
     */
    private Integer total;
    /**
     * 当前页列表
     */
    private List<T> records;

    public PageListDTO(Integer total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
