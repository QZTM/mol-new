package com.mol.supplier.service.uploadAndDownload;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.mol.oos.TYOOSUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class DownloadService {

    @Autowired
    private TYOOSUtil tyoosUtil;

    /**
     * 根据桶名称，路径获取指定路径下的文件list(路径+名称，不包括桶空间名称，例如：xxx供应商名称/战略供应商认证资料/协议图片/aqorcxrdcsgmmvw.jpeg)
     * @param bucket        桶空间名称
     * @param filePre       文件路径
     * @return
     * @throws IOException
     */
    public List<String> getFileNameListFromOOS(String bucket, String filePre) throws IOException {
        log.info("getFileNameListFromOOS");
        ObjectListing objectListing = tyoosUtil.listObj(bucket, filePre);
        List<String> fileNameList = new ArrayList<>();
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            fileNameList.add(objectSummary.getKey());
        }
        return fileNameList;
    }

    public byte[] getFileBytesFromOOS(String bucket,String key) throws IOException {
        return tyoosUtil.download(bucket,key,"name");
    }

}
