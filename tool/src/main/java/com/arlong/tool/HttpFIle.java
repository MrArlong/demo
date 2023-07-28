package com.arlong.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: zzl
 * @date: 2023/7/26 17:52
 **/
@RestController
public class HttpFIle {
    @Resource
    private RestTemplate restTemplate;

    /**
     * @description: 获取接口返回流
     * @author: zzl
     * @date: 2023/7/27 16:36
     * @return: void
     **/
    public void springframeworkGetFileStream() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject(params);

        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), header);
        // 返回值
        String result = restTemplate.postForObject("url", httpEntity, String.class);
        result = restTemplate.getForObject("url?page=${page}", String.class, httpEntity);
        httpEntity = new HttpEntity(params, header);
        // 接收小文件
        ResponseEntity<byte[]> textToVoice = restTemplate.postForEntity("url", httpEntity, byte[].class);
        FileOutputStream outputStream = null;
        outputStream = new FileOutputStream(new File(""));
        outputStream.write(textToVoice.getBody());


        //定义请求头的接收类型
        RequestCallback requestCallback = request -> request.getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
        //异步请求，获取大文件，对响应进行流式处理而不是将其全部加载到内存中
        restTemplate.execute("url", HttpMethod.GET, requestCallback, clientHttpResponse -> {
            Files.copy(clientHttpResponse.getBody(), Paths.get("保存文件地址"));
            return null;
        });
        // 大文件建议
        Files.write(Paths.get("存放文件路径"), Objects.requireNonNull(textToVoice.getBody(), "未获取到下载文件"));
    }

    public void hutoolGetFileStream() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject(params);
        //普通获取返回值
        HttpUtil.post("url", params);
        // 获取文件流
        HttpResponse response = HttpUtil.createPost("url").execute();
        InputStream inputStream = response.bodyStream();
        FileOutputStream outputStream = null;
        outputStream = new FileOutputStream(new File(""));
        IoUtil.copy(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }

    public void springframeworkSendFileStream() {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        FileSystemResource fileSystemResource = new FileSystemResource("url");
        params.add("file", fileSystemResource);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        String result = restTemplate.postForObject("url", requestEntity, String.class);
    }

    public void hutoolSendFileStream(HttpServletResponse response) throws IOException {
        // 被动调用获取文件流
        File file = new File("path");
        FileInputStream is = new FileInputStream(file);
        OutputStream outputStream = response.getOutputStream();
        IoUtil.copy(is, outputStream);
        is.close();
        outputStream.close();

        // 主动发起文件流
        HashMap<String, Object> paramMap = new HashMap<>();
        //文件上传只需将参数中的键指定（默认file），值设为文件对象即可，对于使用者来说，文件上传与普通表单提交并无区别
        paramMap.put("file", FileUtil.file("path"));
        String result = HttpUtil.post("url", paramMap);

    }

}
