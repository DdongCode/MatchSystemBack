package com.jamscoco.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Anonymous;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.jamscoco.domain.Match;
import com.jamscoco.service.IMatchService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 赛事Controller
 *
 * @author jamscoco
 * @date 2022-09-20
 */
@Api("赛事信息管理")
@RestController
@RequestMapping("/match/history")
public class MatchController extends BaseController
{
    @Autowired
    private IMatchService matchService;

    /**
     * 查询历史赛事列表（不包括当前正在进行的赛事）
     */
    @PreAuthorize("@ss.hasPermi('match:history:list')")
    @GetMapping("/list")
    public TableDataInfo list(Match match)
    {
        startPage();
        List<Match> list = matchService.selectHistoryMatchList(match);
        return getDataTable(list);
    }

    /**
     * 导出历史赛事列表
     */
    @PreAuthorize("@ss.hasPermi('match:history:export')")
    @Log(title = "赛事", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Match match)
    {
        List<Match> list = matchService.selectHistoryMatchList(match);
        ExcelUtil<Match> util = new ExcelUtil<Match>(Match.class);
        util.exportExcel(response, list, "赛事数据");
    }

    /**
     * 获取赛事详细信息
     */
    @PreAuthorize("@ss.hasPermi('match:history:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(matchService.getById(id));
    }

    /**
     * 新增赛事
     */
    @PreAuthorize("@ss.hasPermi('match:history:add')")
    @Log(title = "赛事", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Match match)
    {
        return toAjax(matchService.save(match));
    }

    /**
     * 修改赛事
     */
    @PreAuthorize("@ss.hasPermi('match:history:edit')")
    @Log(title = "赛事", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Match match)
    {
        return toAjax(matchService.updateById(match));
    }

    /**
     * 删除赛事
     */
    @PreAuthorize("@ss.hasPermi('match:history:remove')")
    @Log(title = "赛事", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(matchService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 查询当前赛事
     */
    @Anonymous
    @ApiOperation("查询当前赛事")
    @GetMapping("/getCurrentMatch")
    public AjaxResult getCurrentMatch(){
        return AjaxResult.success(matchService.getCurrentMatch());
    }

    /**
     * 主页显示信息查询
     */
    @Anonymous
    @GetMapping("/getIndexInfo")
    public AjaxResult getIndexInfo(){
        Match currentMatch = matchService.getCurrentMatch();
        if(null  == currentMatch){
            return AjaxResult.error("当前没有进行中的赛事");
        }
        return AjaxResult.success(matchService.getIndexInfo(currentMatch));
    }

}
