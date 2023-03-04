package com.wei.application.controller;

import com.wei.application.Service.OcrService;
import com.wei.application.Service.QRCodeService;
import com.wei.application.common.ApiRestResponse;
import com.wei.application.common.Constant;
import com.wei.application.exception.AppException;
import com.wei.application.exception.AppExceptionEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
public class MainController {

    @Resource
    QRCodeService qrCodeService;

    @Resource
    OcrService ocrService;

    /**
     * 测试端口
     */
    @GetMapping("/test")
    public ApiRestResponse text() {
        return ApiRestResponse.success("hi 测试端口成功");
    }

    /**
     * 生成二维码
     */
    @GetMapping("/qrcode")
    public ApiRestResponse qrcode(@RequestParam String text) {
        String pngAddress = qrCodeService.qrcode(text);
        return ApiRestResponse.success(pngAddress);
    }

    /**
     * ocr上传图片地址
     */
    //这里需要传入两个参数: 1.为了在图片地址中保存我们的地址,比如说url、ip等,  2.文件类型。而且这个file要加上注解方便它识别
    @PostMapping("ocr/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) {
        //给图片生成uuid的名字,但后缀是不变的。获取后缀名
        String fileName = file.getOriginalFilename(); //获取原始图片名字
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //生成文件名称UUID
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffix;
        //创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);//文佳夹
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);//目标文件
        //判断文件夹是否存在
        if (!fileDirectory.exists()) {
            //如果创建失败
            if (!fileDirectory.mkdir()) {
                throw new AppException(AppExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //getRequestURL() 得到的是一个StringBuffer,而new URI()方法需要传入一个String类型。所以加上""就可以转为String
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL() + "")) + "/images/" + newFileName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ApiRestResponse.error(AppExceptionEnum.UPLOAD_FAILED);
    }

    //用于获取ip和端口号
    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                    null, null, null);
        } catch (URISyntaxException e) {
            //如果新建的过程中失败了,那么就把它设置为null
            effectiveURI = null;
        }
        //获得我们想要的那部分的信息的uri,而把多余的信息剔除掉
        return effectiveURI;
    }

    /**
     * 拿到ocr识别的文本
     */
    @PostMapping("/getOcrText")
    public ApiRestResponse getOcrText(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) throws Exception {
        //给图片生成uuid的名字,但后缀是不变的。获取后缀名
        String fileName = file.getOriginalFilename(); //获取原始图片名字
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //生成文件名称UUID
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffix;
        //创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);//文佳夹
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);//目标文件
        //判断文件夹是否存在
        if (!fileDirectory.exists()) {
            //如果创建失败
            if (!fileDirectory.mkdir()) {
                throw new AppException(AppExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //getRequestURL() 得到的是一个StringBuffer,而new URI()方法需要传入一个String类型。所以加上""就可以转为String
            String imgUrl = getHost(new URI(httpServletRequest.getRequestURL() + "")) + "/images/" + newFileName;
            String text = ocrService.getRecognizeText(imgUrl);
            return ApiRestResponse.success(text);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ApiRestResponse.error(AppExceptionEnum.UPLOAD_FAILED);
    }
}
