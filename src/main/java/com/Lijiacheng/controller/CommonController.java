package com.Lijiacheng.controller;

import com.Lijiacheng.common.Result;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 * */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    // 注入配置文件的属性
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * */
    @RequestMapping("/upload")
    // 请求参数名是Form-Data的name值
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info(file.toString());
        /*
        * 本地的是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        * */
        String originalFilename = file.getOriginalFilename();   // xxxxxxx.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用UUID重新生成文件名，防止文件名重复造成的文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        /* 判断文件目录basePath确实存在，若不存在则先进行创建 */
        // 创建一个目录对象
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        file.transferTo(new File(basePath + fileName));

        return Result.success(fileName);
    }

    /**
     * 文件下载
     * */
    @GetMapping("/download")
    public void download(HttpServletResponse response, @RequestParam("name") String fileName) throws Exception {
        // 输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath + fileName));
        // 输出流，通过输出流将文件写回浏览器，在浏览器展示图片
        ServletOutputStream outputStream = response.getOutputStream();

        // 设置响应数据的类型
        response.setContentType("image/jpeg");

        int len = 0;
        byte[] bytes = new byte[1024];
        while((len = fileInputStream.read(bytes)) != -1){
            outputStream.write(bytes,0 ,len);
            outputStream.flush();
        }
        // 关闭资源
        outputStream.close();
        fileInputStream.close();
    }
}
