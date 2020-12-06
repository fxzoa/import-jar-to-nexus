package tools.nexus.upload;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

/**
 * @author dy.huang
 * @date 2020/7/25 21:48
 */
public class App {

    private static final String URL = "http://192.168.43.128:8888/service/rest/v1/components?repository=maven-releases";
    private static final String APP_KEY = "username";
    private static final String SECRET_KEY = "passward";
    private static final String FILE_PATH = "C:\\Users\\.m2\\repository";
    private static String filePathJar = null;
    private static String filePathPom = null;
    private static boolean next = false;

    public static void main(String[] args) throws Exception {
        File dirFile = new File(FILE_PATH);
        dirAll(dirFile);
        // 结束后，可能会有最后一组没有被上传
        System.out.println(" =========== 结束 ============");
        upload(filePathJar, filePathPom);
        System.out.println(" =========== 结束 ============");
    }


    private static void dirAll(File dirFile) throws Exception {
        if (dirFile.exists()) {
            File files[] = dirFile.listFiles();
            for (File file : files) {
                //如果遇到文件夹则递归调用。
                if (file.isDirectory()) {
                    next = true;
                    System.out.println("file = " + file.getName());
                    // 递归调用
                    dirAll(file);
                } else {
                    if (next) {
                        upload(filePathJar, filePathPom);
                    }
                    if (file.getAbsolutePath().endsWith(".jar")) {
                        filePathJar = file.getAbsolutePath();
                    }
                    if (file.getAbsolutePath().endsWith(".pom")) {
                        filePathPom = file.getAbsolutePath();
                    }
                    next = false;
                }
            }
        }
    }

    private static void upload(String pathJar, String pathPom) {
        HttpPost post = new HttpPost(URL);
        //设置请求头
        HashMap<String, String> header = new HashMap<>();
        // 认证token
        header.put("Authorization", getHeader());
        for (String key : header.keySet()) {
            post.addHeader(key, header.get(key));
        }

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.RFC6532);
        builder.setCharset(StandardCharsets.UTF_8);
        //构造待上传数据,加入builder
        if (!isEmpty(pathJar)) {
            File file = new File(pathJar);
            builder.addPart("maven2.asset1.extension", new StringBody("jar", ContentType.MULTIPART_FORM_DATA));
            builder.addPart("maven2.asset1", new FileBody(file, ContentType.DEFAULT_BINARY));
            // 匹配命名不规范的一些规则
            if (pathJar.contains("noaop")) {
                builder.addPart("maven2.asset1.classifier", new StringBody("noaop", ContentType.MULTIPART_FORM_DATA));
            }
            if (pathJar.contains("no_aop")) {
                builder.addPart("maven2.asset1.classifier", new StringBody("no_aop", ContentType.MULTIPART_FORM_DATA));
            }
            if (pathJar.contains("linux-x86_64")) {
                builder.addPart("maven2.asset1.classifier", new StringBody("linux-x86_64", ContentType.MULTIPART_FORM_DATA));
            }
        }

        if (!isEmpty(pathPom)) {
            File file1 = new File(pathPom);
            builder.addPart("maven2.asset2.extension", new StringBody("pom", ContentType.MULTIPART_FORM_DATA));
            builder.addPart("maven2.asset2", new FileBody(file1, ContentType.DEFAULT_BINARY));
        }

        filePathJar = null;
        filePathPom = null;
    }

    private static String getHeader() {
        String auth = APP_KEY + ":" + SECRET_KEY;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        return "Basic " + new String(encodedAuth);
    }

    private static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

}
