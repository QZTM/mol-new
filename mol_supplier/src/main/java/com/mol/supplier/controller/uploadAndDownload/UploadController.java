package com.mol.supplier.controller.uploadAndDownload;
import com.mol.oos.OOSConfig;
import com.mol.oos.TYOOSUtil;
import com.mol.supplier.entity.MicroApp.Salesman;
import com.mol.supplier.entity.MicroApp.Supplier;
import com.mol.supplier.service.microApp.MicroUserService;
import com.mol.supplier.service.uploadAndDownload.DelFileService;
import com.mol.supplier.service.uploadAndDownload.UploadService;
import entity.ServiceResult;
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

/**
 * 文件上传控制器
 */
@Controller
@RequestMapping("/file")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

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
    public ServiceResult uploadManyPictures(@RequestParam("file") MultipartFile file, HttpSession session){

        Supplier supplier = microUserService.getSupplierFromSession(session);
        Salesman salesman = microUserService.getUserFromSession(session);

        if (file==null){
            return ServiceResult.failureMsg("文件接收失败");
        }

        String path = supplier.getName()+"/战略供应商认证资料"+"/";
        String bucketName = OOSConfig.供应商文件夹;
        uploadService.uploadToOOS(file,path,bucketName);
        return ServiceResult.successMsg("上传成功");

    }

}