package com.mol.oos;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.java.Log;
import util.FileUtils;
import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
public class TYOOSUtil {

    private AmazonS3 oos = OOSClient.getClient();
    private volatile static TYOOSUtil oosUtil;
    private TYOOSUtil(){
    }

    public static TYOOSUtil getUtil() {
        if (oosUtil == null) {
            synchronized (TYOOSUtil.class) {
                if (oosUtil == null) {
                    oosUtil = new TYOOSUtil();
                }
            }
        }
        return oosUtil;
    }


    /**
     * 列出所有的bucket
     */
    public List<Bucket> bucketList(){
        /*列出账户内的所有 buckets */
        log.info("Listing buckets");
        List<Bucket> buckets = oos.listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println(" - " + bucket.getName());
        }
        return buckets;
    }

    /**
     * 创建bucket
     * @param bucketName
     */
    public void createBucket(String bucketName){
        /* 创建 bucket */
        log.info("Creating bucket " + bucketName + "\n");
        oos.createBucket(bucketName);
        System.out.println();
    }

    /**
     * 上传一个文件
     * @param bucketName        桶容器名称
     * @param key               文件名
     * @param file              文件
     */
    public void uploadObjToBucket(String bucketName,String key,File file) throws IOException {
        /* 上传一个 object 到 bucket 中 */
        log.info("Uploading a new object to OOS from a file,,,bucketName:"+bucketName+",key:"+key);
        PutObjectResult putObjectResult = oos.putObject(new PutObjectRequest(bucketName, key, file));
        System.out.println(putObjectResult.toString());
    }

    /**
     * 下载
     * @param bucketName
     * @param key
     * @throws IOException
     */
    public void download(String bucketName,String key) throws IOException {
        /* 下载 object */
        log.info("Downloading an object");
            /* 当使用getObject()方法时，需要非常小心。因为返回的S3Object对象包
            括一个从HTTP连接获得的数据流。底层的HTTP连接不会被关闭，直到用户
            读完了数据，并关闭了流。因此从S3Object中读取inputStream数据后，需要立刻关闭流。
            否则会导致客户端连接池用满 */
        S3Object object = oos.getObject(new GetObjectRequest(bucketName, key));
        System.out.println("Content-Type: " + object.getObjectMetadata().getContentType());
        System.out.println("Content:");

        String fileName = key.substring(key.lastIndexOf("/"));
        S3ObjectInputStream objectContent = object.getObjectContent();
        FileImageOutputStream fios =new FileImageOutputStream(new File("d:"+File.separator+fileName));

        byte[] bytes=new byte[1024];
        int len=0;
        while ((len=objectContent.read(bytes))!=-1){
            fios.write(bytes,0,len);
        }
        fios.close();
        displayTextInputStream(object.getObjectContent());
    }

    /**
     * 下载文件并存入byte数组中
     * @param bucketName
     * @param key
     * @throws IOException
     */
    public byte[] download(String bucketName,String key,String name) throws IOException {
        /* 下载 object */
        log.info("Downloading an object to byte[]");
            /* 当使用getObject()方法时，需要非常小心。因为返回的S3Object对象包
            括一个从HTTP连接获得的数据流。底层的HTTP连接不会被关闭，直到用户
            读完了数据，并关闭了流。因此从S3Object中读取inputStream数据后，需要立刻关闭流。
            否则会导致客户端连接池用满 */
        S3Object object = oos.getObject(new GetObjectRequest(bucketName, key));
        String fileName = key.substring(key.lastIndexOf("/"));
        S3ObjectInputStream objectContent = object.getObjectContent();
        byte[] bytes1 = FileUtils.toByteArray(objectContent);
        displayTextInputStream(object.getObjectContent());
        return bytes1;
    }


    /**
     *  拷贝 object
     * @param bucketName
     * @param key
     * @param destinationBucketName
     * @param destinationKey
     */
    public void copy(String bucketName,String key,String destinationBucketName,String destinationKey) {
        /* 拷贝 object */
//        String destinationBucketName = "my-copy-oos-bucket";
//        String destinationKey = "MyCopyKey";
        log.info("Copying an object ,from " + bucketName + "/" + key + " to " + destinationBucketName + "/" + destinationKey);
        oos.createBucket(destinationBucketName);
        oos.copyObject(bucketName, key, destinationBucketName, destinationKey);
    }


    /**
     * 下载拷贝的 object
     * @param bucketName
     * @param key
     * @param destinationKey
     * @param destinationBucketName
     * @throws IOException
     */
    public void downCoped(String bucketName,String key,String destinationKey,String destinationBucketName) throws IOException {
        /* 下载拷贝的 object */
        S3Object object = oos.getObject(new GetObjectRequest(bucketName, key));
        log.info("Downloading the " + destinationKey + " object");
        object = oos.getObject(new GetObjectRequest(destinationBucketName, destinationKey));
        System.out.println("Content-Type: " + object.getObjectMetadata().getContentType());
        System.out.println("Content:");
        displayTextInputStream(object.getObjectContent());
    }


    /**
     * 列出 bucket 中的 object
     * @param bucketName        桶名称
     * @param filePre           文件前缀
     */
    public ObjectListing listObj(String bucketName,String filePre){
        /* 列出 bucket 中的 object，支 prefix,delimiter,marker,max-keys 等选项 */
        if(filePre == null){
            filePre = "";
        }
        log.info("Listing objects");
        ObjectListing objectListing = oos.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(filePre));
//        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
//            System.out.println(objectSummary.getBucketName()+" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
//        }
       return objectListing;
    }


    /**
     * 批量下载 bucket 中的 object
     * @param bucketName        桶名称
     * @param filePre           文件前缀
     */
    public  void listDownLoad(String bucketName,String filePre){
        log.info("listDownLoad....bucketName:"+bucketName+",,filePre:"+filePre);
        ObjectListing objectListing = listObj(bucketName,filePre);
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            log.info(objectSummary.getBucketName()+" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
            try {
                download(objectSummary.getBucketName(),objectSummary.getKey());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    /**
     * 批量下载 bucket 中的 object
     * @param bucketName        桶名称
     * @param filePre           文件前缀
     */
    public Map<String,byte[]> listDownLoad(String bucketName, String filePre, String name){
        log.info("listDownLoad.To..Map...bucketName:"+bucketName+",,filePre:"+filePre);
        ObjectListing objectListing = listObj(bucketName,filePre);
        Map<String,byte[]> objs = new HashMap<>();
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            log.info(objectSummary.getBucketName()+" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
            try {
                byte[] names = download(objectSummary.getBucketName(), objectSummary.getKey(), "name");
                objs.put(objectSummary.getKey(),names);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return objs;
    }




    /**
     * 删除 object
     * @param bucketName
     * @param key
     * @param destinationBucketName
     * @param destinationKey
     */
    public void delObj(String bucketName,String key,String destinationBucketName,String destinationKey){
        /* 删除 object */
        System.out.println("Deleting objects\n");
        oos.deleteObject(bucketName, key);
        oos.deleteObject(destinationBucketName, destinationKey);
    }


    /**
     * 删除bucket
     * @param bucketName
     * @param destinationBucketName
     */
    public void delBucket(String bucketName,String destinationBucketName){
        /* 删除 bucket */
        System.out.println("Deleting bucket " + bucketName + "\n");
        oos.deleteBucket(bucketName);
        System.out.println("Deleting bucket " + destinationBucketName + "\n");
        oos.deleteBucket(destinationBucketName);
    }


    public static File createSampleFile() throws IOException {
        File file = File.createTempFile("oos-java-sdk-", ".txt");
        file.deleteOnExit();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghuvwxyz\n");
        writer.write("0122345678901234\n");
        writer.write("!@#$':',.<>/?\n");
        writer.write("012345678901234\n");
        writer.write("abcderstuvwxyz\n");
        writer.close();
        return file;
    }


    public void getBucketLocation(String bucketName){
        String locationResult = oos.getBucketLocation(bucketName);
        System.out.println("bucketLocation:"+locationResult);
    }


    /**
     * 关闭资源占用
     * @param input
     * @throws IOException
     */
    public void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) {break;}
            System.out.println("    " + line);
        } /* 需要在这里关闭InputStream，原因参见getObject处 */
        input.close();
        System.out.println();
    }

}
