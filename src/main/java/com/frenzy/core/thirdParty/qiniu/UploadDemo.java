package com.frenzy.core.thirdParty.qiniu;

import com.alibaba.fastjson.JSON;
import com.frenzy.core.config.UploadConfig;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UploadDemo {
    //设置好账号的ACCESS_KEY和SECRET_KEY
//    String ACCESS_KEY = Config.QINIU_ACCESS_KEY;
//    String SECRET_KEY = Config.QINIU_SECRET_KEY;
//    //要上传的空间
//    String bucketname = Config.QINIU_bucketname;
//    @Resource
//    QiniuCloudPublicConfig qiniuCloudPublicConfig = new QiniuCloudPublicConfig();
    //    @Resource
//    YfCmsConfig11 yfCmsConfig11;
    private static Map<String,BucketManager.FileListIterator> fileListIteratorMap = new HashMap<>();

    //密钥配置
    Auth auth = Auth.create(UploadConfig.accessKey, UploadConfig.secretKey);

    ///////////////////////指定上传的Zone的信息//////////////////
    //第一种方式: 指定具体的要上传的zone
    //注：该具体指定的方式和以下自动识别的方式选择其一即可
    //要上传的空间(bucket)的存储区域为华东时
    // Zone z = Zone.zone0();
    //要上传的空间(bucket)的存储区域为华北时
    // Zone z = Zone.zone1();
    //要上传的空间(bucket)的存储区域为华南时
    // Zone z = Zone.zone2();

    //第二种方式: 自动识别要上传的空间(bucket)的存储区域是华东、华北、华南。
    Zone z = Zone.autoZone();
    Configuration c = new Configuration(z);

    //创建上传对象
    UploadManager uploadManager = new UploadManager(c);

    public static void main(String args[]) throws IOException {
//        new UploadDemo().upload();
    }

    public static String main(byte[] args, String key) throws IOException {
        return new UploadDemo().upload(args, key);
    }


    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        return auth.uploadToken(UploadConfig.bucket);
    }

    public String upload(byte[] args, String key) throws IOException {
        try {
            //调用put方法上传
            Response res = uploadManager.put(args, key, getUpToken());
            //打印返回的信息
            System.out.println(res.bodyString());
            return res.bodyString();
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());

            try {
                //响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException e1) {
                //ignore
            }
        }
        return null;
    }


    public static List<String> list(String getId){
        return new UploadDemo().fileListIterator(getId);
    }
    public List<String> fileListIterator(String getId) {
        List<String> objectList=new ArrayList<>();
        BucketManager.FileListIterator fileListIterator = fileListIteratorMap.get(getId);
        if (fileListIterator == null) {
            //文件名前缀
            String prefix = UploadConfig.Upload_Folder_name;
            //每次迭代的长度限制，最大1000，推荐值 1000
            int limit = 20;
            //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
            String delimiter = "";

            BucketManager bucketManager = new BucketManager(auth, c);
            //列举空间文件列表
            fileListIterator = bucketManager.createFileListIterator(UploadConfig.bucket, prefix, limit, delimiter);
            fileListIteratorMap.put(getId, fileListIterator);
        }
        if (fileListIterator.hasNext()) {
            //处理获取的file list结果
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                objectList.add(UploadConfig.domain+item.key);
//                System.out.println(item.key);
//                System.out.println(item.hash);
//                System.out.println(item.fsize);
//                System.out.println(item.mimeType);
//                System.out.println(item.putTime);
//                System.out.println(item.endUser);
            }
        }
        return objectList;
    }

    public static String fetch(String url,String fileName){
        return new UploadDemo().fetchTask(url, fileName);
    }

    public String fetchTask(String url,String fileName) {
        //实例化一个BucketManager对象
        BucketManager bucketManager = new BucketManager(auth, c);
        try {
            //调用fetch方法抓取文件
            FetchRet fetchRet = bucketManager.fetch(url, UploadConfig.bucket, fileName);
            return JSON.toJSONString(fetchRet);
        } catch (QiniuException e) {
            //捕获异常信息
            Response r = e.response;
            System.out.println(r.toString());
            return JSON.toJSONString(r.toString());
        }
    }


}