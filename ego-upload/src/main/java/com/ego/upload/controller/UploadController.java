package com.ego.upload.controller;

import com.ego.upload.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 *
 **/
@Controller
@RequestMapping("/upload")
@Slf4j
public class UploadController {
    @Resource
    private UploadService uploadService;
    @PostMapping("/image")
    @ResponseBody
    public String upload(MultipartFile file){
        if (null == file){
            log.info("文件为空");
            return null;
        }
        return uploadService.upload(file);
    }
}
