package com.mol.supplier.service.uploadAndDownload;

import com.mol.oos.OOSConfig;
import com.mol.oos.TYOOSUtil;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import util.TimeUtil;
import util.UploadUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

@Log
@Service
public class UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    @Autowired
    private TYOOSUtil tyoosUtil;

    /**
     * 上传文件
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            return "文件为空";
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        //logger.info("上传的文件名为：" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //logger.info("上传的后缀名为：" + suffixName);
        // 文件上传后的路径
        //根据文件后缀名获取文件类型存放的文件夹
        String dir = UploadUtils.getRealDir(suffixName);

        // 存放上传文件的文件夹
        File fileDir = UploadUtils.getDirFile();

        //存放当前种类文件的文件夹：
        File fileDirOfFileType = UploadUtils.getFileDirOfSave(suffixName);



        String filePath = fileDirOfFileType.getAbsolutePath() + File.separator + TimeUtil.getNowOnlyDate();
        //System.out.println("filePath:"+filePath);
        // 解决中文问题，liunx下中文路径，图片显示问题
        //fileName = UUID.randomUUID() + suffixName;
        String newFileName = UUID.randomUUID()+suffixName;
        File dest = new File(filePath + "//" + newFileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            String returnPath = "/"+UploadUtils.PATH_PREFIX+dir+"/"+ TimeUtil.getNowOnlyDate()+"/"+newFileName;
            //System.out.println(returnPath);
            return returnPath;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            throw new RuntimeException("上传文件出错");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("上传文件出错");
        }
    }


    @Async
    public Future<String> uploadToOOS(MultipartFile multipartFile, String path, String bucketName){
//获取名字
        String fileName = multipartFile.getOriginalFilename();
        //获取后缀
        String  suffName= fileName.substring(fileName.lastIndexOf("."));
        //产生一个新名字
        String name = RandomStringUtils.random(15,"asdfghjkqwertyuiopzxcvbnm")+suffName;
        //获取当前class的路径
        URL resource = UploadService.class.getResource("");
        File file=new File(resource.getPath()+File.separator+name);
        try {
            //m 转 f
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(),file);
            File newFile=new File(resource.getPath()+File.separator+name);
            //文件夹
            log.info("上传OOS之前：bucketName："+bucketName+",,,key:"+path+name+",newfile.name:"+newFile.getName()+",newFile.size:"+newFile.length());
            tyoosUtil.uploadObjToBucket(bucketName,path+name,newFile);
            return new AsyncResult<>(name);
        } catch (IOException e) {
            logger.info("合同照片上传异常");
            e.printStackTrace();
            return new AsyncResult<>("");
        }finally {
            file.delete();
        }
    }


    /**
     * 从天翼云上删除一个文件
     * @param filepath
     */
    public void delFileFromOOS(String bucketName,String filepath){
        log.info("删除OOS文件...bucketName:"+bucketName+",filepath:"+filepath);
            tyoosUtil.delObj(bucketName,filepath,"","");
    }





    //文件下载相关代码
    public String downloadFile(HttpServletRequest request, HttpServletResponse response) {
        String fileName = "FileUploadTests.java";
        if (fileName != null) {
            //当前是从该工程的WEB-INF//File//下获取文件(该目录可以在下面一行代码配置)然后下载到C:\\users\\downloads即本机的默认下载的目录
            String realPath = request.getServletContext().getRealPath(
                    "//WEB-INF//");
            File file = new File(realPath, fileName);
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition",
                        "attachment;fileName=" + fileName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    System.out.println("success");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    //多文件上传
    @RequestMapping(value = "/batch/upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request)
                .getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    stream = new BufferedOutputStream(new FileOutputStream(
                            new File(file.getOriginalFilename())));
                    stream.write(bytes);
                    stream.close();

                } catch (Exception e) {
                    stream = null;
                    return "You failed to upload " + i + " => "
                            + e.getMessage();
                }
            } else {
                return "You failed to upload " + i
                        + " because the file was empty.";
            }
        }
        return "upload successful";
    }
}
