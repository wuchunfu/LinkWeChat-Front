package com.linkwechat.web.controller.wecom;

import com.linkwechat.common.annotation.Log;
import com.linkwechat.common.core.controller.BaseController;
import com.linkwechat.common.core.domain.AjaxResult;
import com.linkwechat.common.core.page.TableDataInfo;
import com.linkwechat.common.enums.BusinessType;
import com.linkwechat.common.enums.MediaType;
import com.linkwechat.wecom.domain.WeMaterial;
import com.linkwechat.wecom.domain.dto.ResetCategoryDto;
import com.linkwechat.wecom.domain.dto.TemporaryMaterialDto;
import com.linkwechat.wecom.domain.dto.WeMediaDto;
import com.linkwechat.wecom.domain.vo.WeMaterialFileVO;
import com.linkwechat.wecom.service.IWeMaterialService;
import com.linkwechat.wecom.service.IWePosterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业微信素材Controller
 *
 * @author KEWEN
 * @date 2020-10-08
 */
@Api("企业微信素材管理")
@RestController
@RequestMapping("/wecom/material")
public class WeMaterialController extends BaseController {


    @Autowired
    private IWeMaterialService materialService;

    @Resource
    private IWePosterService wePosterService;


    //   @PreAuthorize("@ss.hasPermi('wecom:material:list')")
    @GetMapping("/list")
    @ApiOperation("查询素材列表")
    public TableDataInfo list(@RequestParam(value = "categoryId", required = false) String categoryId
            , @RequestParam(value = "search", required = false) String search,@RequestParam(value = "mediaType") String mediaType) {
        startPage();
        List<WeMaterial> list;
        if(StringUtils.isNotBlank(mediaType) && mediaType.equals(MediaType.POSTER.getType())){
            list = wePosterService.list(StringUtils.isBlank(categoryId)?null:Long.valueOf(categoryId),search).stream().map(wePoster -> {
                    WeMaterial weMaterial = new WeMaterial();
                    weMaterial.setMaterialName(wePoster.getTitle());
                    weMaterial.setMaterialUrl(wePoster.getSampleImgPath());
                    weMaterial.setCategoryId(wePoster.getCategoryId());
                    weMaterial.setId(wePoster.getId());
                    return weMaterial;
            }).collect(Collectors.toList());
        }else {
            list = materialService.findWeMaterials(categoryId, search,mediaType);
        }

        return getDataTable(list);
    }



    //  @PreAuthorize("@ss.hasPermi('wechat:material:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("查询素材详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(materialService.findWeMaterialById(id));
    }


    //   @PreAuthorize("@ss.hasPermi('wechat:material:add')")
    @Log(title = "添加素材信息", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加素材信息")
    public AjaxResult add(@RequestBody WeMaterial material) {
        return toAjax(materialService.insertWeMaterial(material));
    }


    //   @PreAuthorize("@ss.hasPermi('wechat:material:edit')")
    @Log(title = "更新素材信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("更新素材信息")
    public AjaxResult edit(@RequestBody WeMaterial material) {
        return toAjax(materialService.updateWeMaterial(material));
    }


    //   @PreAuthorize("@ss.hasPermi('wechat:material:remove')")
    @Log(title = "删除素材信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiOperation("删除素材信息")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(materialService.deleteWeMaterialByIds(ids));
    }


    //   @PreAuthorize("@ss.hasPermi('wechat:material:upload')")
    @Log(title = "上传素材信息", businessType = BusinessType.OTHER)
    @PostMapping("/upload")
    @ApiOperation("上传素材信息")
    public AjaxResult upload(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "mediaType") String mediaType) {
        WeMaterialFileVO weMaterialFileVO = materialService.uploadWeMaterialFile(file, mediaType);
        return AjaxResult.success(weMaterialFileVO);
    }


    //   @PreAuthorize("@ss.hasPermi('wechat:material:resetCategory')")
    @Log(title = "更换分组", businessType = BusinessType.OTHER)
    @PutMapping("/resetCategory")
    @ApiOperation("更换分组")
    public AjaxResult resetCategory(@RequestBody ResetCategoryDto resetCategoryDto) {
        materialService.resetCategory(resetCategoryDto.getCategoryId(), resetCategoryDto.getMaterials());
        return AjaxResult.success();
    }

//    //@PreAuthorize("@ss.hasPermi('wechat:material:temporaryMaterialMediaId')")
//    @Log(title = "获取素材media_id", businessType = BusinessType.OTHER)
//    @PostMapping("/temporaryMaterialMediaId")
//    @ApiOperation("获取素材media_id")
//    public AjaxResult temporaryMaterialMediaId(@RequestBody TemporaryMaterialDto temporaryMaterialDto){
//        WeMediaDto weMediaDto = materialService.uploadTemporaryMaterial(temporaryMaterialDto.getUrl(),
//                temporaryMaterialDto.getType()
//                ,temporaryMaterialDto.getName());
//        return AjaxResult.success(weMediaDto);
//    }

    //@PreAuthorize("@ss.hasPermi('wechat:material:temporaryMaterialMediaId')")
    @Log(title = "获取素材media_id", businessType = BusinessType.OTHER)
    @GetMapping("/temporaryMaterialMediaId")
    @ApiOperation("获取素材media_id")
    public AjaxResult temporaryMaterialMediaId(String url,String type,String name){
        WeMediaDto weMediaDto = materialService.uploadTemporaryMaterial(url,
                type
                ,name);
        return AjaxResult.success(weMediaDto);
    }


    @Log(title = "上传素材图片", businessType = BusinessType.OTHER)
    @PostMapping("/uploadimg")
    @ApiOperation("上传素材图片")
    public AjaxResult<WeMediaDto> uploadImg(MultipartFile file){
         WeMediaDto weMediaDto=new WeMediaDto();
//        WeMediaDto weMediaDto = materialService.uploadImg(file);
//        weMediaDto.setFileName(file.getResource().getFilename());
        WeMaterialFileVO weMaterialFileVO = materialService.uploadWeMaterialFile(file, MediaType.IMAGE.getType());
        weMediaDto.setFileName(weMaterialFileVO.getMaterialName());
        weMediaDto.setUrl(weMaterialFileVO.getMaterialUrl());
        return AjaxResult.success(weMediaDto);
    }

}
