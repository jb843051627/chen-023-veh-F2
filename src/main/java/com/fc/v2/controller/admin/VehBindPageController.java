package com.fc.v2.controller.admin;

import com.fc.v2.common.base.BaseController;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.log.Log;
import com.fc.v2.model.custom.vehbind.VehBindPageCarry;
import com.fc.v2.model.custom.vehbind.VehBindPageCarryResult;
import com.fc.v2.model.custom.vehbind.VehBindPageForm;
import com.fc.v2.model.custom.vehbind.VehBindPageQuery;
import com.fc.v2.model.custom.vehbind.VehBindPageSaveResult;
import com.fc.v2.model.custom.vehbind.VehBindPageScreen;
import com.fc.v2.service.ITVehBindPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 车辆联网清单页 Controller。
 * 这一层不写任何判路：立不立得住、轧出几、屏上见几页、页头三数，全照业务服务层
 * saveBindPage / screen 写回的结果显；接口层不留第二道能绕过它的门。
 *
 * @author fuce
 * @date 2026-09-12
 */
@Api(value = "车辆联网清单页")
@Controller
@RequestMapping("/VehBindPageController")
public class VehBindPageController extends BaseController {

    private final String prefix = "admin/vehBindPage";

    @Autowired
    private ITVehBindPageService vehBindPageService;

    @ApiOperation(value = "分页跳转", notes = "分页跳转")
    @GetMapping("/view")
    @RequiresPermissions("veh:vehBindPage:view")
    public String view(ModelMap model) {
        return prefix + "/list";
    }

    @Log(title = "车辆联网清单页本层一屏", action = "screen")
    @ApiOperation(value = "本层那一屏", notes = "页头三栏与名单同一次算出；县不见邻县、市不掺别市；三个找头前缀对上也算")
    @GetMapping("/screen")
    @RequiresPermissions("veh:vehBindPage:list")
    @ResponseBody
    public AjaxResult screen(VehBindPageQuery query) {
        VehBindPageScreen screen = vehBindPageService.screen(query);
        if (screen == null) {
            return error("层级没圈定（只认 1县 2市 3省），这一屏不开");
        }
        return success(200, "本层一屏", screen);
    }

    @Log(title = "车辆联网清单页进门", action = "save")
    @ApiOperation(value = "立页/改口/换层一道门", notes = "头回立页、已联改口、页换挂靠层同走 saveBindPage；号系统排、差系统轧，毛病逐格退回")
    @PostMapping("/save")
    @RequiresPermissions("veh:vehBindPage:add")
    @ResponseBody
    public AjaxResult save(@RequestBody VehBindPageForm form) {
        VehBindPageSaveResult result = vehBindPageService.saveBindPage(form);
        if (result.isSuccess()) {
            return success(200, "这一页落成", result.getPage());
        }
        // 进不了库的真进不了库：毛病点明在哪一格，光回"数不对劲"不算
        AjaxResult err = AjaxResult.error(400, "这一页落不进库，毛病逐格列明");
        err.put("fieldErrors", result.getFieldErrors());
        return err;
    }

    @Log(title = "当月核收带出立页", action = "carry")
    @ApiOperation(value = "从当月核收带出页", notes = "立页第二条路子；来路册在核、册上点得到车才带，每张仍走 saveBindPage")
    @PostMapping("/carry")
    @RequiresPermissions("veh:vehBindPage:add")
    @ResponseBody
    public AjaxResult carry(@RequestBody VehBindPageCarry param) {
        VehBindPageCarryResult summary =
                vehBindPageService.carryFromReceipt(param == null ? null : param.getBatchNo(),
                        param == null ? null : param.getPages());
        return success(200, "当月核收带出办结", summary);
    }

    @Log(title = "车辆联网清单页删除", action = "remove")
    @ApiOperation(value = "逻辑删除", notes = "在册页落删除标记留痕，不物理抹行")
    @DeleteMapping("/remove")
    @RequiresPermissions("veh:vehBindPage:remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(vehBindPageService.deleteTVehBindPageByIds(ids));
    }
}
