package com.boot.controller;

import com.boot.util.HttpUtil;
import com.boot.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.Producer;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class VerifyCodeController {
    @Autowired
    private RedisTemplate redisTemplate;
    private final Producer producer;
    @Autowired
    public VerifyCodeController(Producer producer) {
        this.producer = producer;
    }

    @PostMapping("/vc.jpg")
    public String getVerifyCode(HttpSession session, HttpServletRequest request) throws IOException {
        //1.生成验证码
        String text = producer.createText();
        if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {//判断是否是 json 格式请求类型
            Object uname = new ObjectMapper().readValue(request.getInputStream(), Map.class).get("uname");
            if (uname != null) {
                //2.放入 redis
                RedisUtil.setKeyValue(uname.toString(), text, redisTemplate);
                //3.生成图片
                BufferedImage bi = producer.createImage(text);
                FastByteArrayOutputStream fos = new FastByteArrayOutputStream();
                ImageIO.write(bi, "jpg", fos);
                //4.返回 base64
                return Base64.encodeBase64String(fos.toByteArray());
            }
        }
        return rspErrorMsg();
    }

    private String rspErrorMsg() throws JsonProcessingException {
        Map<String, Object> result = new HashMap<>();
        result.put("msg", "获取验证码失败");
        result.put("code", Code.SYSTEM_ERR);
        return new ObjectMapper().writeValueAsString(result);
    }
}
