package com.mol.supplier.controller.uploadAndDownload;
import com.mol.oos.OOSConfig;
import com.mol.oos.TYOOSUtil;
import com.mol.supplier.entity.MicroApp.Salesman;
import com.mol.supplier.entity.MicroApp.Supplier;
import com.mol.supplier.service.microApp.MicroUserService;
import com.mol.supplier.service.uploadAndDownload.DelFileService;
import com.mol.supplier.service.uploadAndDownload.UploadService;
import entity.ServiceResult;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

/**
 * 文件上传控制器
 */
@Log
@Controller
@RequestMapping("/file")
@CrossOrigin
public class UploadController {

    static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    static final String PROTYPE_ZHANLVE = "zhanlve";
    static final String PROTYPE_DANYI = "danyi";
    static final String PROTOCOLPATHMIDDLE_ZHANLVE = "/战略供应商认证资料/协议图片/";
    static final String PROTOCOLPATHMIDDLE_DANYI = "/单一供应商认证资料/协议图片/";

    @Resource
    private UploadService uploadService;

    @Autowired
    private DelFileService delFileService;

    @Autowired
    private MicroUserService microUserService;





    //文件上传相关代码
    @RequestMapping(value = "upload")
    @ResponseBody
    public ServiceResult<String> upload(@RequestParam("file") MultipartFile file) {
        String newPath = uploadService.upload(file);
        logger.info("UploadController返回值："+newPath);
        return ServiceResult.success(newPath);

    }

    //文件下载相关代码
    @RequestMapping("/download")
    public String downloadFile(HttpServletRequest request, HttpServletResponse response) {

        return uploadService.downloadFile(request,response);

    }

    //多文件上传
    @RequestMapping(value = "/batch/upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(HttpServletRequest request) {
        return uploadService.handleFileUpload(request);
    }


    @RequestMapping(value = "/del",method = RequestMethod.DELETE)
    @ResponseBody
    public ServiceResult delFile(String filePath, HttpServletRequest request){
        String path = request.getServletContext().getRealPath("/");
        return delFileService.delFile(filePath);
    }

    @PostMapping("/uploadMany")
    @ResponseBody
    public ServiceResult uploadManyPictures(@RequestParam("file") MultipartFile file,String proType,HttpSession session) throws ExecutionException, InterruptedException {
        Supplier supplier = microUserService.getSupplierFromSession(session);
        Salesman salesman = microUserService.getUserFromSession(session);
        log.info("多图片上传。。。。proType:"+proType);
        if (file==null){
            return ServiceResult.failureMsg("没有文件");
        }
        System.out.println(file.getName());
        System.out.println(file.getSize());
        System.out.println(file.getOriginalFilename());
        String filePathMiddle = "";
        switch (proType){
            case PROTYPE_ZHANLVE:
                filePathMiddle = PROTOCOLPATHMIDDLE_ZHANLVE;
                break;
            case PROTYPE_DANYI:
                filePathMiddle = PROTOCOLPATHMIDDLE_DANYI;
                break;
        }
        String path = supplier.getName()+filePathMiddle;
        String bucketName = OOSConfig.供应商文件夹;
        String uploadNewFileName = uploadService.uploadToOOS(file,path,bucketName).get();
        if(!StringUtils.isEmpty(uploadNewFileName)){
            return ServiceResult.success("上传成功",uploadNewFileName);
        }else{
            return ServiceResult.failureMsg("上传失败");
        }


    }

    /**
     * 删除OOS上的图片（战略供应商协议/单一供应商协议图片）
     * @param filename      文件名称
     * @param proType       协议类型，zhanlve或者danyi
     * @return
     */
    @ResponseBody
    @RequestMapping("/oosdel")
    public ServiceResult delFileFromOOs(String filename,String proType,HttpSession session){
        Supplier supplier = microUserService.getSupplierFromSession(session);
        log.info("删除天翼云oos上的文件,filename:"+filename+",proType:"+proType);
        String filepath = "";
        switch (proType){
            case PROTYPE_ZHANLVE:
                filepath = filepath + supplier.getName()+PROTOCOLPATHMIDDLE_ZHANLVE+filename;
                break;
            case PROTYPE_DANYI:
                filepath = filepath + supplier.getName()+PROTOCOLPATHMIDDLE_DANYI+filename;
                break;
        }
        try{
            log.info("去OOS上删除一个文件，filepath:"+filepath);
            uploadService.delFileFromOOS(OOSConfig.供应商文件夹,filepath);
            return ServiceResult.successMsg("删除成功");
        }catch(Exception e){
            log.warning(e.getMessage());
            return ServiceResult.failureMsg("删除失败");
        }
    }





}