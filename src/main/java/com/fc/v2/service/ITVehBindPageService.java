package com.fc.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.fc.v2.model.auto.TVehBindPage;

import java.util.List;

/**
 * 车辆联网清单页 Service接口
 *
 * @author fuce
 * @date 2026-09-12
 */
public interface ITVehBindPageService {

    /** 按主键查询 */
    TVehBindPage selectTVehBindPageById(Long id);

    /** 按条件查询列表（分页由调用方统一处理） */
    List<TVehBindPage> selectTVehBindPageList(Wrapper<TVehBindPage> queryWrapper);

    /** 新增 */
    int insertTVehBindPage(TVehBindPage record);

    /** 修改 */
    int updateTVehBindPage(TVehBindPage record);

    /** 批量删除 */
    int deleteTVehBindPageByIds(String ids);

    /** 按主键删除 */
    int deleteTVehBindPageById(Long id);

    /**
     * 推进一态（待核对 → 已核对 → 已冻住）。
     * 清单页的进展按三态推进；已冻住的页不再往前推。
     * 参数表与返回值以本行为准，实现方需给出真实推进逻辑。
     */
    default int advanceTVehBindPage(Long id) {
        throw new UnsupportedOperationException("推进一态的逻辑尚未实现");
    }

    /**
     * 退回一态（已核对 → 待核对，已冻住 → 已核对）；旧记录沉底留痕。
     * 参数表与返回值以本行为准，实现方需给出真实退回逻辑。
     */
    default int revertTVehBindPage(Long id) {
        throw new UnsupportedOperationException("退回一态的逻辑尚未实现");
    }
}
