package com.fc.v2.model.custom.vehbind;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一张页进门后的回话。
 * 进不了库的得真进不了库：落不下时 fieldErrors 一格一格里点明毛病出在哪一格，
 * 光回一句"数不对劲"不算；落得下时 page 回显轧完新数后的整页。
 *
 * @author fuce
 * @date 2026-10-04
 */
@ApiModel(value = "VehBindPageSaveResult", description = "清单页进门回话")
public class VehBindPageSaveResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 成没成（一页要么整页落库，要么整页退回） */
    @ApiModelProperty(value = "成没成")
    private boolean success;

    /** 毛病出在哪一格：格名 → 缘故 */
    @ApiModelProperty(value = "逐格退回缘故")
    private Map<String, String> fieldErrors = new LinkedHashMap<String, String>();

    /** 落成后回显的整页（系统排的号、轧出的差都在上面） */
    @ApiModelProperty(value = "落成后回显的整页")
    private VehBindPageVo page;

    public static VehBindPageSaveResult fail(String field, String reason) {
        VehBindPageSaveResult r = new VehBindPageSaveResult();
        r.success = false;
        r.fieldErrors.put(field, reason);
        return r;
    }

    public VehBindPageSaveResult addError(String field, String reason) {
        this.fieldErrors.put(field, reason);
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public VehBindPageVo getPage() {
        return page;
    }

    public void setPage(VehBindPageVo page) {
        this.page = page;
    }
}
