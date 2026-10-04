package com.fc.v2.model.custom.vehbind;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 当月核收带出页的汇总回话：来路册不成立时一张也不带；
 * 册在，就逐张走 saveBindPage，每张成不成、毛病在哪一格，与亲手立页拿到的回话一模一样。
 *
 * @author fuce
 * @date 2026-10-04
 */
@ApiModel(value = "VehBindPageCarryResult", description = "核收带出页汇总回话")
public class VehBindPageCarryResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 当月核收册码 */
    @ApiModelProperty(value = "当月核收册码")
    private String batchNo;

    /** 来路册批注：无此当月核收册时写明 */
    @ApiModelProperty(value = "来路册批注")
    private String mark;

    /** 递来几页 */
    @ApiModelProperty(value = "递来几页")
    private int srcCount;

    /** 落成几页 */
    @ApiModelProperty(value = "落成几页")
    private int savedCount;

    /** 退回几页 */
    @ApiModelProperty(value = "退回几页")
    private int failedCount;

    /** 逐页回话（顺序与递来的页一致） */
    @ApiModelProperty(value = "逐页回话")
    private List<VehBindPageSaveResult> results = new ArrayList<VehBindPageSaveResult>();

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getSrcCount() {
        return srcCount;
    }

    public void setSrcCount(int srcCount) {
        this.srcCount = srcCount;
    }

    public int getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(int savedCount) {
        this.savedCount = savedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public List<VehBindPageSaveResult> getResults() {
        return results;
    }

    public void setResults(List<VehBindPageSaveResult> results) {
        this.results = results;
    }
}
