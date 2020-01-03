package com.mol.supplier.controller.uploadAndDownload;

import com.mol.oos.OOSConfig;
import com.mol.supplier.entity.MicroApp.Supplier;
import com.mol.supplier.service.microApp.MicroUserService;
import com.mol.supplier.service.uploadAndDownload.DownloadService;
import entity.ServiceResult;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
@Controller
@CrossOrigin
@RequestMapping("/down")
public class DownloadController {

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private MicroUserService microUserService;


    /**
     * 获取协议图片
     * @param proType       协议类型，如战略供应商协议、单一供应商协议
     * @param session
     * @return
     */
    @RequestMapping("/proImages")
    @ResponseBody
    public ServiceResult getProImages(String proType, HttpSession session){
        Supplier supplier = microUserService.getSupplierFromSession(session);
        //获取OOS指定路径下的文件列表
        List<String> fileNameList = new ArrayList<>();
        Map<String,byte[]> fileBytes = new HashMap<>();
        String filePre = "";
        switch (proType){
            case UploadController.PROTYPE_ZHANLVE:
                filePre = UploadController.PROTOCOLPATHMIDDLE_ZHANLVE;
                break;
            case UploadController.PROTYPE_DANYI:
                filePre = UploadController.PROTOCOLPATHMIDDLE_DANYI;
                break;
        }
        try {
            fileNameList = downloadService.getFileNameListFromOOS(OOSConfig.供应商文件夹, supplier.getName() + filePre);
            //遍历列表，根据key获取对应的文件输入流，并把输入流转化为byte[]
            for(String str : fileNameList){
                log.info("遍历获取到的文件名称："+str);
                byte[] fileBytesFromOOS = downloadService.getFileBytesFromOOS(OOSConfig.供应商文件夹, str);
                fileBytes.put(str.substring(str.lastIndexOf("/")+1),fileBytesFromOOS);
            }
        } catch (IOException e) {
            log.warning("获取OOS的文件列表出错，，proType:"+proType);
        }
        //返回ServiceResult对象，其result为Map<String,byte[]>类型，
        return ServiceResult.success("下载成功",fileBytes);
    }
}
