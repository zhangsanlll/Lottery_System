package com.example.lotterysystem.service.impl;

import com.example.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.example.lotterysystem.common.exception.ServiceException;
import com.example.lotterysystem.service.PictureService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class PictureServiceImpl implements PictureService {

    @Value("${pic.local-path}")
    private String localPath;

    @Override
    public String savePicture(MultipartFile pic) {
        //创建目录
        File dir = new File(localPath);
        if(!dir.exists()){
            dir.mkdirs();//若是创建目录的父目录没有，则会自动创建
        }

        //创建索引
        //1、先拿到图片名称  2、再拿图片的后缀.jpg(其他格式的后缀也可以) .jpg
        // 3、随机生成一个数字名xxx  4、最后拼接  xxx.jpg
        String fileName = pic.getOriginalFilename();
        assert fileName!= null;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        fileName = UUID.randomUUID() + suffix;

        //图片保存
        try {
            pic.transferTo(new File(localPath + "/" +fileName));
        } catch (IOException e) {
            throw new ServiceException(ServiceErrorCodeConstants.PIC_UPLOAD_ERROR);
        }
        return fileName;
    }
}
