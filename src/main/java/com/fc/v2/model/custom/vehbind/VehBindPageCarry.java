package com.fc.v2.model.custom.vehbind;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 从当月核收册里带页：册码标明来路，册下行里点到的车辆营运代号才带得出；
 * 每张页的报文与亲手立页完全一样，两道路子同走 saveBindPage 一套数法，界面上没有第二种样子。
 *
 * @author fuce
 * @date 2026-10-04
 */
@ApiModel(value = "VehBindPageCarry", description = "当月核收带出页报文")
public class VehBindPageCarry implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 当月核收册码（来路） */
    @ApiModelProperty(value = "当月核收册码")
    private String batchNo;

    /** 要带出的页（与亲手立页同一张报文） */
    @ApiModelProperty(value = "要带出的页")
    private List<VehBindPageForm> pages = new ArrayList<VehBindPageForm>();

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public List<VehBindPageForm> getPages() {
        return pages;
    }

    public void setPages(List<VehBindPageForm> pages) {
        this.pages = pages;
    }
}
