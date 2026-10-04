package com.fc.v2.model.custom.vehbind;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 一层那一屏：页头三栏 + 本层名单（分页）。
 * 三栏按本层全部在册页一次点出，名单可再按三个头找；两边出自同一套库查，不借行、不抹行。
 *
 * @author fuce
 * @date 2026-10-04
 */
@ApiModel(value = "VehBindPageScreen", description = "一层清单页屏")
public class VehBindPageScreen implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 本层层级序 1县 2市 3省 */
    @ApiModelProperty(value = "本层层级序 1县 2市 3省")
    private Integer nodeNo;

    /** 页头三栏（按本层在册页统点，不随找头缩） */
    @ApiModelProperty(value = "页头三栏")
    private VehBindPageOverview overview;

    /** 本层名单这一页摆的几条 */
    @ApiModelProperty(value = "本层名单分页数据")
    private List<VehBindPageVo> rows;

    /** 找完头后名单总共几条（分页用） */
    @ApiModelProperty(value = "名单总条数")
    private long total;

    public Integer getNodeNo() {
        return nodeNo;
    }

    public void setNodeNo(Integer nodeNo) {
        this.nodeNo = nodeNo;
    }

    public VehBindPageOverview getOverview() {
        return overview;
    }

    public void setOverview(VehBindPageOverview overview) {
        this.overview = overview;
    }

    public List<VehBindPageVo> getRows() {
        return rows;
    }

    public void setRows(List<VehBindPageVo> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
