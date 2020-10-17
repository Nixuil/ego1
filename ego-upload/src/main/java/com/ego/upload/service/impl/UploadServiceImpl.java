package com.ego.upload.service.impl;

import com.ego.upload.service.UploadService;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 **/
@Service
@Slf4j
public class UploadServiceImpl implements UploadService {
    private final List<String> suffixs = Arrays.asList("image/jpg", "image/png");
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Override
    public String upload(MultipartFile file) {
        try {
            if (!suffixs.contains(file.getContentType())) {
                log.info("文件格式不正确{}",file.getContentType());
                return null;
            }
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (null == image){
                log.info("文件内容不符合格式");
                return null;
            }
            String suffixString = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), suffixString, null);
            log.info("文件上传成功，带组路径{}"+storePath.getFullPath());
            return "http://image.ego.com/"+storePath.getFullPath();
        } catch (IOException e) {
            log.error("文件上传失败{}",e);
            return null;
        }
    }
}
