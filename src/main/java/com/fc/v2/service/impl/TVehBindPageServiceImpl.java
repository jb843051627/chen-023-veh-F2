package com.fc.v2.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.common.conf.V2Config;
import com.fc.v2.common.support.ConvertUtil;
import com.fc.v2.mapper.auto.TVehAlarmRowMapper;
import com.fc.v2.mapper.auto.TVehBindPageMapper;
import com.fc.v2.mapper.auto.TVehEntMapper;
import com.fc.v2.model.auto.TVehAlarmBatch;
import com.fc.v2.model.auto.TVehAlarmRow;
import com.fc.v2.model.auto.TVehBindPage;
import com.fc.v2.model.auto.TVehEnt;
import com.fc.v2.model.custom.vehbind.VehBindPageCarryResult;
import com.fc.v2.model.custom.vehbind.VehBindPageForm;
import com.fc.v2.model.custom.vehbind.VehBindPageOverview;
import com.fc.v2.model.custom.vehbind.VehBindPageQuery;
import com.fc.v2.model.custom.vehbind.VehBindPageSaveResult;
import com.fc.v2.model.custom.vehbind.VehBindPageScreen;
import com.fc.v2.model.custom.vehbind.VehBindPageVo;
import com.fc.v2.service.ITVehAlarmBatchService;
import com.fc.v2.service.ITVehBindPageService;
import com.fc.v2.util.SnowflakeIdWorker;
import com.fc.v2.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 车辆联网清单页 Service业务层处理。
 * 立不立得住、还差几项轧出几、屏上见哪几页、页头三数打哪来——四样判路全在这一层，
 * 接口层只照回话办事，库里实有几页就显几页，不借行、不抹行。
 *
 * @author fuce
 * @date 2026-09-12
 */
@Service
public class TVehBindPageServiceImpl extends ServiceImpl<TVehBindPageMapper, TVehBindPage>
        implements ITVehBindPageService {

    /** 处里定的每页条数缺省兜底 */
    private static final int DEFAULT_PAGE_SIZE = 10;
    /** 每页条数的顶，防着有人把 limit 撑得没边 */
    private static final int MAX_PAGE_SIZE = 500;
    /** 系统排号撞号时最多再排几把 */
    private static final int NO_MAX_RETRY = 5;

    /** 清单代号前缀（系统排号） */
    private static final String BILL_NO_PREFIX = "QD";
    /** 随页签认码前缀（系统排号） */
    private static final String SIGN_CODE_PREFIX = "QR";

    private static final Pattern INTEGER_TEXT = Pattern.compile("\\d+");

    @Autowired
    private TVehEntMapper vehEntMapper;

    @Autowired
    private TVehAlarmRowMapper vehAlarmRowMapper;

    @Autowired
    private ITVehAlarmBatchService vehAlarmBatchService;

    @Autowired
    private V2Config v2Config;

    // ============================ 唯一的写入判路 ============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VehBindPageSaveResult saveBindPage(VehBindPageForm form) {
        VehBindPageSaveResult result = new VehBindPageSaveResult();

        if (form == null) {
            return result.addError("_form", "空报文，这一页无处可落");
        }

        // 车辆营运代号：一车一页的那个"车"
        String vehNo = trimToNull(form.getVehNo());
        if (vehNo == null) {
            result.addError("vehNo", "车辆营运代号空着递，这一页落不进库");
        }

        // 业户先认总名录代号；销户只卡新立，旧页改口不被销户牵动（见下）
        String siteNo = trimToNull(form.getSiteNo());
        TVehEnt ent = null;
        if (siteNo == null) {
            result.addError("siteNo", "所属业户代号空着递，这一页落不进库");
        } else {
            ent = vehEntMapper.selectOne(new QueryWrapper<TVehEnt>()
                    .eq("site_no", siteNo).eq("del_flag", 0).last("limit 1"));
            if (ent == null) {
                result.addError("siteNo", "业户代号不在总名录上，代号不统一名录，页起不来");
            }
        }

        // 层级：县、市、省三层各管自家那一片
        Integer nodeNo = form.getNodeNo();
        if (nodeNo == null || nodeNo < NODE_COUNTY || nodeNo > NODE_PROVINCE) {
            result.addError("nodeNo", "页挂靠层级只认 1县 2市 3省");
        }

        // 应联、已联：原样文本逐格点验，空/零头/负数一格一格报明白
        Integer shouldCount = parseCount(form.getShouldText(), "shouldCount", "应联项数", result);
        Integer doneCount = parseCount(form.getDoneText(), "doneCount", "已联项数", result);
        if (shouldCount != null && doneCount != null && doneCount > shouldCount) {
            // 同一件事的两面，按进门那条规矩归到"应联"这一格的四样毛病里点明：
            // 应联低过同页已联数（即已联冒过应联），整页收不进来
            result.addError("shouldCount", "应联项数低过同页已联项数（已联冒过应联），这一页收不进来");
        }

        // 改口/换层：先认在册旧页；立页：认一车一页
        TVehBindPage existing = null;
        boolean creating = form.getId() == null;
        if (form.getId() != null) {
            existing = this.baseMapper.selectById(form.getId());
            if (existing == null || (existing.getDelFlag() != null && existing.getDelFlag() == 1)) {
                result.addError("id", "这一页不在册，没法改口或换层");
            } else if (vehNo != null && !vehNo.equals(existing.getVehNo())) {
                result.addError("vehNo", "改口只许改联项数与挂靠层，不许把页挪到另一辆车");
            } else if (siteNo != null && !siteNo.equals(existing.getSiteNo())) {
                result.addError("siteNo", "改口只许改联项数与挂靠层，不许改换所属业户");
            }
        } else if (vehNo != null) {
            Integer dup = this.baseMapper.selectCount(new QueryWrapper<TVehBindPage>()
                    .eq("veh_no", vehNo).eq("del_flag", 0));
            if (dup != null && dup > 0) {
                result.addError("vehNo", "这辆车名下已有在册清单页，一车只许立一张");
            }
        }

        // 销户业户名下不许再立新页；此前立过的照旧在、照旧算数
        if (creating && ent != null && ent.getStatus() != null && ent.getStatus() == 1) {
            result.addError("siteNo", "该业户已销户，名下不许再立新页（旧页照旧在册）");
        }

        if (!result.getFieldErrors().isEmpty()) {
            // 进不了库的真进不了库：整页退回，库里一行不动，毛病逐格点明
            return result;
        }

        // 还差几项由系统轧：应联 - 已联；齐平写零。谁递来的这一格都不认
        int lack = shouldCount - doneCount;
        int status = rollStatus(shouldCount, doneCount);

        if (creating) {
            TVehBindPage page = new TVehBindPage();
            page.setVehNo(vehNo);
            page.setSiteId(ent.getId() == null ? null : ent.getId().intValue());
            page.setSiteNo(siteNo);
            page.setNodeNo(nodeNo);
            page.setShouldCount(shouldCount);
            page.setDoneCount(doneCount);
            page.setLackCount(lack);
            page.setStatus(status);
            page.setContent(trimToNull(form.getContent()));
            page.setRemark(trimToNull(form.getRemark()));
            page.setDelFlag(0);
            // 清单代号、随页签认码两样由系统排号，本子自带号码一概不认
            page.setBillNo(nextBillNo());
            page.setSignCode(nextSignCode());
            this.baseMapper.insert(page);
            result.setSuccess(true);
            result.setPage(toVo(page, ent));
            return result;
        }

        // 改口（已联项数改口）、换层（页换挂靠那一层）：新轧的数覆盖旧数
        existing.setNodeNo(nodeNo);
        existing.setShouldCount(shouldCount);
        existing.setDoneCount(doneCount);
        existing.setLackCount(lack);
        existing.setStatus(status);
        if (form.getContent() != null) {
            existing.setContent(trimToNull(form.getContent()));
        }
        if (form.getRemark() != null) {
            existing.setRemark(trimToNull(form.getRemark()));
        }
        existing.setUpdateTime(new Date());
        this.baseMapper.updateById(existing);
        result.setSuccess(true);
        result.setPage(toVo(existing, ent != null ? ent : loadEnt(existing.getSiteNo())));
        return result;
    }

    /**
     * 数格原样点验：空着递、带零头、是负数各自报各自那格；过了才给整数。
     */
    private Integer parseCount(String text, String field, String label, VehBindPageSaveResult result) {
        String t = trimToNull(text);
        if (t == null) {
            result.addError(field, label + "空着递，这一页落不进库");
            return null;
        }
        if (t.startsWith("-")) {
            result.addError(field, label + "是负数，这一页落不进库");
            return null;
        }
        if (!INTEGER_TEXT.matcher(t).matches()) {
            result.addError(field, label + "带零头（不是整数），这一页落不进库");
            return null;
        }
        try {
            long v = Long.parseLong(t);
            if (v > Integer.MAX_VALUE) {
                result.addError(field, label + "大得离了谱，这一页落不进库");
                return null;
            }
            return (int) v;
        } catch (NumberFormatException ex) {
            result.addError(field, label + "念得不成数，这一页落不进库");
            return null;
        }
    }

    /**
     * 进展由两数当场轧：两数齐平→已联齐；一项没联→新录；联了没齐→缺项。
     */
    private int rollStatus(int should, int done) {
        if (done >= should) {
            return STATUS_FULL;
        }
        return done == 0 ? STATUS_NEW : STATUS_LACK;
    }

    private String nextBillNo() {
        return nextSerialNo(BILL_NO_PREFIX, "bill_no");
    }

    private String nextSignCode() {
        return nextSerialNo(SIGN_CODE_PREFIX, "sign_code");
    }

    /**
     * 系统排号：雪花号加前缀，库里再照一面，撞了重排。
     */
    private String nextSerialNo(String prefix, String column) {
        for (int i = 0; i < NO_MAX_RETRY; i++) {
            String no = prefix + SnowflakeIdWorker.getUUID();
            Integer cnt = this.baseMapper.selectCount(new QueryWrapper<TVehBindPage>()
                    .eq(column, no).eq("del_flag", 0));
            if (cnt == null || cnt == 0) {
                return no;
            }
        }
        throw new IllegalStateException("系统排号连续撞号，这一页先退回重递");
    }

    // ============================ 立页第二条路子（同义方法） ============================

    @Override
    public VehBindPageCarryResult carryFromReceipt(String batchNo, List<VehBindPageForm> pages) {
        VehBindPageCarryResult summary = new VehBindPageCarryResult();
        summary.setBatchNo(batchNo);
        List<VehBindPageForm> safePages = pages == null ? new ArrayList<VehBindPageForm>() : pages;
        summary.setSrcCount(safePages.size());

        // 来路先认当月核收册：已撤册、无此册的，一张也不带
        TVehAlarmBatch batch = StringUtils.isNotEmpty(batchNo)
                ? vehAlarmBatchService.selectActiveByBatchNo(batchNo) : null;
        if (batch == null) {
            summary.setMark("无此在核的当月核收册（或已撤册），一张也带不出来");
            summary.setSavedCount(0);
            summary.setFailedCount(safePages.size());
            for (VehBindPageForm p : safePages) {
                summary.getResults().add(VehBindPageSaveResult.fail("batchNo", summary.getMark()));
            }
            return summary;
        }

        // 只从"当月"核收里带：册上记着报送年月的，须与眼下同月；没记年月的不武断拦
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int nowYear = cal.get(java.util.Calendar.YEAR);
        int nowMonth = cal.get(java.util.Calendar.MONTH) + 1;
        boolean monthMismatch = (batch.getReportYear() != null && batch.getReportYear() != nowYear)
                || (batch.getReportMonth() != null && batch.getReportMonth() != nowMonth);
        if (monthMismatch) {
            summary.setMark("核收册「" + batchNo + "」不是当月册，当月带页只从当月册里带");
            summary.setSavedCount(0);
            summary.setFailedCount(safePages.size());
            for (VehBindPageForm p : safePages) {
                summary.getResults().add(VehBindPageSaveResult.fail("batchNo", summary.getMark()));
            }
            return summary;
        }

        // 册上点到、且真收进来的车辆（未勾/已收下）才带得走；退回行点不到
        List<TVehAlarmRow> rows = vehAlarmRowMapper.selectList(new QueryWrapper<TVehAlarmRow>()
                .eq("batch_no", batchNo).eq("del_flag", 0)
                .in("status", ITVehAlarmBatchService.ROW_PENDING, ITVehAlarmBatchService.ROW_ACCEPTED));
        Set<String> carriedVehs = new HashSet<String>();
        for (TVehAlarmRow r : rows) {
            if (StringUtils.isNotEmpty(r.getItemCode())) {
                carriedVehs.add(r.getItemCode().trim());
            }
        }

        int saved = 0;
        int failed = 0;
        for (VehBindPageForm form : safePages) {
            String vehNo = form == null ? null : trimToNull(form.getVehNo());
            if (vehNo == null || !carriedVehs.contains(vehNo)) {
                summary.getResults().add(VehBindPageSaveResult.fail(
                        "vehNo", "当月核收册「" + batchNo + "」上点不到这辆车，带不出来"));
                failed++;
                continue;
            }
            // 两道路子同走这一道门，数法、校验、回话一模一样
            VehBindPageSaveResult one = saveBindPage(form);
            summary.getResults().add(one);
            if (one.isSuccess()) {
                saved++;
            } else {
                failed++;
            }
        }
        summary.setSavedCount(saved);
        summary.setFailedCount(failed);
        return summary;
    }

    // ============================ 本层那一屏 ============================

    @Override
    public VehBindPageScreen screen(VehBindPageQuery query) {
        VehBindPageScreen screen = new VehBindPageScreen();
        if (query == null || query.getNodeNo() == null
                || query.getNodeNo() < NODE_COUNTY || query.getNodeNo() > NODE_PROVINCE) {
            // 层级没圈定，一屏也不借：交接口层按"层级没给"退回
            return null;
        }
        screen.setNodeNo(query.getNodeNo());

        // 业户类别与属地这两个找头都落在名录上，合成一道点出本岗可见的业户代号集合：
        // 县岗只见本县属地、市岗不掺别市；一个都点不到就是空名单（也不借别县的行）
        List<String> scopeSiteNos = null;
        boolean hasType = StringUtils.isNotEmpty(query.getSiteType());
        boolean hasRoad = StringUtils.isNotEmpty(query.getRoadName());
        if (hasType || hasRoad) {
            QueryWrapper<TVehEnt> ew = new QueryWrapper<TVehEnt>().eq("del_flag", 0);
            if (hasType) {
                ew.eq("site_type", query.getSiteType().trim());
            }
            if (hasRoad) {
                // 按"省—市—县"那一路的前缀收口：省/市/县岗各见自家那一片
                ew.likeRight("road_name", query.getRoadName().trim());
            }
            scopeSiteNos = new ArrayList<String>();
            for (TVehEnt e : vehEntMapper.selectList(ew)) {
                if (StringUtils.isNotEmpty(e.getSiteNo())) {
                    scopeSiteNos.add(e.getSiteNo());
                }
            }
            if (scopeSiteNos.isEmpty()) {
                screen.setOverview(new VehBindPageOverview(0, 0, 0));
                screen.setRows(new ArrayList<VehBindPageVo>());
                screen.setTotal(0);
                return screen;
            }
        }

        // 页头三栏与名单同一套条件、同一次回点：挂页车数 - 已联齐 = 缺项，天然对得平；
        // 不带找头时点的就是本层全部，本层没立过页这里就是三栏零
        long total = countByConditions(query, scopeSiteNos, null);
        long complete = countByConditions(query, scopeSiteNos, 0);
        long lack = total - complete;
        screen.setOverview(new VehBindPageOverview(total, complete, lack));

        int pageSize = resolvePageSize(query.getLimit());
        int pageNum = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        PageHelper.startPage(pageNum, pageSize);
        List<TVehBindPage> pages = this.baseMapper.selectList(buildConditions(query, scopeSiteNos, null));
        PageInfo<TVehBindPage> info = new PageInfo<TVehBindPage>(pages);

        screen.setRows(toVoList(pages));
        screen.setTotal(info.getTotal());
        return screen;
    }

    private long countByConditions(VehBindPageQuery query, List<String> scopeSiteNos, Integer lackEq) {
        Integer cnt = this.baseMapper.selectCount(buildConditions(query, scopeSiteNos, lackEq));
        return cnt == null ? 0L : cnt.longValue();
    }

    /**
     * 本层在册页的统一点法：层级与属地两道收口、del_flag=0，三个找头只在开头对上就算，
     * lackEq=0 点已联齐；不传按全部点。三栏与名单都从这一套条件出。
     */
    private QueryWrapper<TVehBindPage> buildConditions(VehBindPageQuery query, List<String> scopeSiteNos,
                                                       Integer lackEq) {
        QueryWrapper<TVehBindPage> w = new QueryWrapper<TVehBindPage>()
                .eq("del_flag", 0)
                .eq("node_no", query.getNodeNo());
        if (StringUtils.isNotEmpty(query.getBillNo())) {
            w.likeRight("bill_no", query.getBillNo().trim());
        }
        if (StringUtils.isNotEmpty(query.getVehNo())) {
            w.likeRight("veh_no", query.getVehNo().trim());
        }
        if (scopeSiteNos != null) {
            w.in("site_no", scopeSiteNos);
        }
        if (lackEq != null) {
            w.eq("lack_count", lackEq.intValue());
        }
        w.orderByDesc("id");
        return w;
    }

    private int resolvePageSize(Integer limit) {
        Integer configured = v2Config.getVehBindPageSize();
        int size = (configured != null && configured > 0) ? configured : DEFAULT_PAGE_SIZE;
        if (limit != null && limit > 0) {
            size = limit;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    // ============================ 回看与删除 ============================

    @Override
    public TVehBindPage selectTVehBindPageById(Long id) {
        if (id == null) {
            return null;
        }
        return this.baseMapper.selectOne(new QueryWrapper<TVehBindPage>()
                .eq("id", id).eq("del_flag", 0));
    }

    @Override
    public List<TVehBindPage> selectTVehBindPageList(Wrapper<TVehBindPage> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int deleteTVehBindPageByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return 0;
        }
        Long[] idArr = ConvertUtil.toLongArray(ids);
        if (idArr == null || idArr.length == 0) {
            return 0;
        }
        // 只在在册行上落删除标记，旧行沉底留痕，不物理抹
        TVehBindPage upd = new TVehBindPage();
        upd.setDelFlag(1);
        upd.setUpdateTime(new Date());
        return this.baseMapper.update(upd, new UpdateWrapper<TVehBindPage>()
                .in("id", Arrays.asList(idArr)).eq("del_flag", 0));
    }

    // ============================ 页与名录的照面 ============================

    private List<VehBindPageVo> toVoList(List<TVehBindPage> pages) {
        if (pages == null || pages.isEmpty()) {
            return new ArrayList<VehBindPageVo>();
        }
        Set<String> siteNos = new HashSet<String>();
        for (TVehBindPage p : pages) {
            if (StringUtils.isNotEmpty(p.getSiteNo())) {
                siteNos.add(p.getSiteNo());
            }
        }
        Map<String, TVehEnt> entMap = loadEntMap(siteNos);
        List<VehBindPageVo> vos = new ArrayList<VehBindPageVo>();
        for (TVehBindPage p : pages) {
            vos.add(toVo(p, entMap.get(p.getSiteNo())));
        }
        return vos;
    }

    private TVehEnt loadEnt(String siteNo) {
        if (StringUtils.isEmpty(siteNo)) {
            return null;
        }
        return loadEntMap(Collections.singleton(siteNo)).get(siteNo);
    }

    /**
     * 按代号回名录照面，销户行也照得出来——销户业户名下旧页照旧列、全称类别属地照旧回显。
     */
    private Map<String, TVehEnt> loadEntMap(Set<String> siteNos) {
        Map<String, TVehEnt> map = new HashMap<String, TVehEnt>();
        if (siteNos == null || siteNos.isEmpty()) {
            return map;
        }
        List<TVehEnt> ents = vehEntMapper.selectList(new QueryWrapper<TVehEnt>()
                .in("site_no", siteNos).eq("del_flag", 0));
        for (TVehEnt e : ents) {
            // 同代号理论上只一行；真有重复也取头一行，不叫屏炸了
            if (!map.containsKey(e.getSiteNo())) {
                map.put(e.getSiteNo(), e);
            }
        }
        return map;
    }

    private VehBindPageVo toVo(TVehBindPage p, TVehEnt ent) {
        VehBindPageVo vo = new VehBindPageVo();
        vo.setId(p.getId());
        vo.setBillNo(p.getBillNo());
        vo.setSignCode(p.getSignCode());
        vo.setVehNo(p.getVehNo());
        vo.setNodeNo(p.getNodeNo());
        vo.setSiteNo(p.getSiteNo());
        vo.setShouldCount(p.getShouldCount());
        vo.setDoneCount(p.getDoneCount());
        vo.setLackCount(p.getLackCount());
        vo.setContent(p.getContent());
        vo.setStatus(p.getStatus());
        vo.setCreateTime(p.getCreateTime());
        if (ent != null) {
            vo.setSiteName(ent.getSiteName());
            vo.setSiteType(ent.getSiteType());
            vo.setRoadName(ent.getRoadName());
        }
        return vo;
    }

    private String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
