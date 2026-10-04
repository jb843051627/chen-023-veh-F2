package com.fc.v2.model.custom.vehbind;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 屏上摆的一页：清单页上的项照原样回显，业户全称、业户类别、属地那一路随名录带出来。
 * 销户业户名下此前立过的页照旧列得出来、照旧算在数内，故这三样按落页时存下的名录口径回显，
 * 名录后来销户也不把旧页抹去。
 *
 * @author fuce
 * @date 2026-10-04
 */
@ApiModel(value = "VehBindPageVo", description = "清单页屏上回看对象")
public class VehBindPageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 页主键 */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "页主键")
    private Long id;

    /** 清单代号（系统排号） */
    @ApiModelProperty(value = "清单代号")
    private String billNo;

    /** 随页签认码（系统排号） */
    @ApiModelProperty(value = "随页签认码")
    private String signCode;

    /** 车辆营运代号 */
    @ApiModelProperty(value = "车辆营运代号")
    private String vehNo;

    /** 页挂靠层级 1县 2市 3省 */
    @ApiModelProperty(value = "页挂靠层级 1县 2市 3省")
    private Integer nodeNo;

    /** 所属业户代号 */
    @ApiModelProperty(value = "所属业户代号")
    private String siteNo;

    /** 所属业户全称（名录带出） */
    @ApiModelProperty(value = "所属业户全称")
    private String siteName;

    /** 业户类别（名录带出） */
    @ApiModelProperty(value = "业户类别")
    private String siteType;

    /** 属地那一路，写到县（名录带出） */
    @ApiModelProperty(value = "属地那一路")
    private String roadName;

    /** 应联项数 */
    @ApiModelProperty(value = "应联项数")
    private Integer shouldCount;

    /** 已联项数 */
    @ApiModelProperty(value = "已联项数")
    private Integer doneCount;

    /** 还差几项（只回显系统轧出来的新数） */
    @ApiModelProperty(value = "还差几项")
    private Integer lackCount;

    /** 随页交来的联网要件 */
    @ApiModelProperty(value = "随页交来的联网要件")
    private String content;

    /** 联网进展 0新录 1已联齐 2缺项 */
    @ApiModelProperty(value = "联网进展 0新录 1已联齐 2缺项")
    private Integer status;

    /** 立页时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "立页时间")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getSignCode() {
        return signCode;
    }

    public void setSignCode(String signCode) {
        this.signCode = signCode;
    }

    public String getVehNo() {
        return vehNo;
    }

    public void setVehNo(String vehNo) {
        this.vehNo = vehNo;
    }

    public Integer getNodeNo() {
        return nodeNo;
    }

    public void setNodeNo(Integer nodeNo) {
        this.nodeNo = nodeNo;
    }

    public String getSiteNo() {
        return siteNo;
    }

    public void setSiteNo(String siteNo) {
        this.siteNo = siteNo;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public Integer getShouldCount() {
        return shouldCount;
    }

    public void setShouldCount(Integer shouldCount) {
        this.shouldCount = shouldCount;
    }

    public Integer getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(Integer doneCount) {
        this.doneCount = doneCount;
    }

    public Integer getLackCount() {
        return lackCount;
    }

    public void setLackCount(Integer lackCount) {
        this.lackCount = lackCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
