package com.fc.v2.model.custom.vehbind;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 页头三栏：挂页的车数、已联齐的、缺项的。
 * 三数与一张一张点出来的名单同一次算出来；本层这一季没立过页就是三栏零，不许借行撑数。
 *
 * @author fuce
 * @date 2026-10-04
 */
@ApiModel(value = "VehBindPageOverview", description = "页头三栏数")
public class VehBindPageOverview implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 挂页的车数 */
    @ApiModelProperty(value = "挂页的车数")
    private long boundCount;

    /** 已联齐的车数 */
    @ApiModelProperty(value = "已联齐的车数")
    private long completeCount;

    /** 缺项的车数（含新录未齐的） */
    @ApiModelProperty(value = "缺项的车数")
    private long lackCount;

    public VehBindPageOverview() {
    }

    public VehBindPageOverview(long boundCount, long completeCount, long lackCount) {
        this.boundCount = boundCount;
        this.completeCount = completeCount;
        this.lackCount = lackCount;
    }

    public long getBoundCount() {
        return boundCount;
    }

    public void setBoundCount(long boundCount) {
        this.boundCount = boundCount;
    }

    public long getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(long completeCount) {
        this.completeCount = completeCount;
    }

    public long getLackCount() {
        return lackCount;
    }

    public void setLackCount(long lackCount) {
        this.lackCount = lackCount;
    }
}
