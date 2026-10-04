package com.fc.v2.model.custom.vehbind;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 找页的三个头：清单代号、车辆营运代号、业户类别；整段对上算找着，只对上开头那几笔也算。
 * nodeNo 圈定本层那一片——县岗那一屏不见邻县的页，市域名单不掺别市的车。
 *
 * @author fuce
 * @date 2026-10-04
 */
@ApiModel(value = "VehBindPageQuery", description = "清单页找页报文")
public class VehBindPageQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 页挂靠层级 1县 2市 3省（必给，本层只看本层那一片） */
    @ApiModelProperty(value = "页挂靠层级 1县 2市 3省")
    private Integer nodeNo;

    /** 找头一：清单代号（前缀对上也算） */
    @ApiModelProperty(value = "清单代号(前缀匹配)")
    private String billNo;

    /** 找头二：车辆营运代号（前缀对上也算） */
    @ApiModelProperty(value = "车辆营运代号(前缀匹配)")
    private String vehNo;

    /** 找头三：业户类别（道路旅客运输/危险货物运输/普通货运） */
    @ApiModelProperty(value = "业户类别")
    private String siteType;

    /**
     * 本岗属地那一路，写到本层止：县岗传"省—市—县"只见本县，市岗传"省—市"不掺别市，
     * 省岗传"省"。按名录属地前缀收口。
     */
    @ApiModelProperty(value = "本岗属地那一路(省—市—县前缀)")
    private String roadName;

    /** 第几页（每页几条由处里定，缺省给个兜底） */
    @ApiModelProperty(value = "第几页")
    private Integer page;

    /** 每页几条 */
    @ApiModelProperty(value = "每页几条")
    private Integer limit;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public Integer getNodeNo() {
        return nodeNo;
    }

    public void setNodeNo(Integer nodeNo) {
        this.nodeNo = nodeNo;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getVehNo() {
        return vehNo;
    }

    public void setVehNo(String vehNo) {
        this.vehNo = vehNo;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }
}
