package com.arlong.tool;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: zzl
 * @date: 2023/7/26 17:52
 **/
@RestController
public class Http {
    @Resource
    private RestTemplate restTemplate;

    /**
     * @description: 获取接口返回流
     * @author: zzl
     * @date: 2023/7/27 16:36
     * @return: void
     **/
    public void getFileStream() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject(params);

        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), header);
        //------------------------springframework
        // 返回值
        String result = restTemplate.postForObject("url", httpEntity, String.class);
        result = restTemplate.getForObject("url?page=${page}", String.class, httpEntity);
        httpEntity = new HttpEntity(params, header);
        ResponseEntity<byte[]> textToVoice = restTemplate.postForEntity("url", httpEntity, byte[].class);
        FileOutputStream outputStream = null;
        outputStream = new FileOutputStream(new File(""));
        outputStream.write(textToVoice.getBody());


        // --------------------- hutool方式
        //获取返回值
        HttpUtil.post("/api/meeting/getMeetingInfo", params);
        // 获取文件流
        HttpResponse response = HttpUtil.createPost("/api/aigov/gw/downloadFile?storageId=").execute();
        InputStream inputStream = response.bodyStream();
        IoUtil.copy(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }
}
