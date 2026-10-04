package com.fc.v2.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.fc.v2.model.auto.TVehBindPage;
import com.fc.v2.model.custom.vehbind.VehBindPageCarryResult;
import com.fc.v2.model.custom.vehbind.VehBindPageForm;
import com.fc.v2.model.custom.vehbind.VehBindPageQuery;
import com.fc.v2.model.custom.vehbind.VehBindPageSaveResult;
import com.fc.v2.model.custom.vehbind.VehBindPageScreen;

/**
 * 车辆联网清单页 Service接口。
 * 一页立不立得住、还差几项轧出几、按属地那一路屏上该见哪几页、页头那三个数打哪儿来——
 * 四样全归 {@link #saveBindPage} 与 {@link #screen} 作答，接口层与页面上不留第二处判路。
 *
 * @author fuce
 * @date 2026-09-12
 */
public interface ITVehBindPageService {

    /** 页所在层级：县（属地写到县为止） */
    int NODE_COUNTY = 1;
    /** 页所在层级：市 */
    int NODE_CITY = 2;
    /** 页所在层级：省 */
    int NODE_PROVINCE = 3;

    /** 联网进展：新录（头一回立页） */
    int STATUS_NEW = 0;
    /** 联网进展：已联齐（应联已联两数齐平） */
    int STATUS_FULL = 1;
    /** 联网进展：缺项（还差几项大于零） */
    int STATUS_LACK = 2;

    /**
     * 一页进门——头一回立页、已联项数改口、页换挂靠那一层，三种时候走同一道门、同一套数法。
     * 这一条是唯一的写入判路，参数表与返回值定死不改；想换一种立页姿势，另起同义方法接住（见
     * {@link #carryFromReceipt}），不许在接口层或页面上绕过它。
     *
     * 进门规矩：
     * 清单代号、随页签认码由系统排号，本子自带号码一律不认；还差几项谁都不填，由系统按
     * 应联减已联轧出，轧出的新数覆盖旧数；应联空着递、带零头、是负数或低过同页已联数，
     * 已联冒过应联，整页落不进库，fieldErrors 里逐格点明毛病出在哪一格。
     * 业户先对总名录代号：代号不在名录页起不来；已销户业户名下不许再立新页，
     * 它此前立过的页照旧在、照旧算数。一辆车只许一张在册页。
     * 页成与不成，回话里都带回轧完后的整页或逐格缘故。
     */
    VehBindPageSaveResult saveBindPage(VehBindPageForm form);

    /**
     * 本层那一屏：页头三栏（挂页车数、已联齐、缺项）与一张一张点出来的名单同一次算出来。
     * 县岗不见邻县的页、市域不掺别市的车；三个找头（清单代号、车辆营运代号、业户类别）
     * 整段对上算找着，只对上开头几笔也算；本层没立过页就是三栏零、名单空，不借行、不抹行。
     * 分页由调用方在调用前用 PageHelper 压好，名单总条数取分页回点；三栏数不随找头缩。
     */
    VehBindPageScreen screen(VehBindPageQuery query);

    /**
     * 立页第二条路子——从当月核收册里带出来。这是"另起的同义方法"：
     * 核验来路册在核、册上确有该车后，每张页原封不动交给 {@link #saveBindPage}，
     * 数法、校验、回话与亲手立页完全一样，界面上没有第二种样子。
     * 一张页退回不牵动同批别页。
     */
    VehBindPageCarryResult carryFromReceipt(String batchNo, List<VehBindPageForm> pages);

    /** 按主键查在册页 */
    TVehBindPage selectTVehBindPageById(Long id);

    /** 按条件查在册页列表（条件由调用方组，分页由调用方统一压） */
    List<TVehBindPage> selectTVehBindPageList(Wrapper<TVehBindPage> queryWrapper);

    /** 逻辑删除在册页（留痕，不物理抹行） */
    int deleteTVehBindPageByIds(String ids);
}
