package com.fc.v2.model.custom.vehbind;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 一张联网清单页进门报文（亲手立页与核收带出走同一道门、同一套数法）。
 * 清单代号、随页签认码、还差几项三格不摆录入框，这里也不收；id 只在改口/换层时带着。
 * 应联、已联两格用原样文本接进来，带零头、负数才点得出是哪一格的毛病，不由接口层截断。
 *
 * @author fuce
 * @date 2026-10-04
 */
@ApiModel(value = "VehBindPageForm", description = "车辆联网清单页进门报文")
public class VehBindPageForm implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 页主键：改口、换层时带着；头回立页为空 */
    @ApiModelProperty(value = "页主键(改口/换层时带)")
    private Long id;

    /** 车辆营运代号（一车一页） */
    @ApiModelProperty(value = "车辆营运代号")
    private String vehNo;

    /** 所属业户代号（对不上总名录代号的，页起不来） */
    @ApiModelProperty(value = "所属业户代号")
    private String siteNo;

    /** 页挂靠层级 1县 2市 3省（属地写到县为止） */
    @ApiModelProperty(value = "页挂靠层级 1县 2市 3省")
    private Integer nodeNo;

    /** 应联项数（原样文本：空着递、带零头、负数都要逐格退回） */
    @ApiModelProperty(value = "应联项数(原样文本)")
    private String shouldText;

    /** 已联项数（原样文本） */
    @ApiModelProperty(value = "已联项数(原样文本)")
    private String doneText;

    /** 随页交来的联网要件 */
    @ApiModelProperty(value = "随页交来的联网要件")
    private String content;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehNo() {
        return vehNo;
    }

    public void setVehNo(String vehNo) {
        this.vehNo = vehNo;
    }

    public String getSiteNo() {
        return siteNo;
    }

    public void setSiteNo(String siteNo) {
        this.siteNo = siteNo;
    }

    public Integer getNodeNo() {
        return nodeNo;
    }

    public void setNodeNo(Integer nodeNo) {
        this.nodeNo = nodeNo;
    }

    public String getShouldText() {
        return shouldText;
    }

    public void setShouldText(String shouldText) {
        this.shouldText = shouldText;
    }

    public String getDoneText() {
        return doneText;
    }

    public void setDoneText(String doneText) {
        this.doneText = doneText;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
