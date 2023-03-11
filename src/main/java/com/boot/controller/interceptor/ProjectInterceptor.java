package com.boot.controller.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


@Component
//定义拦截器类，实现HandlerInterceptor接口
//注意当前类必须受Spring容器控制
public class ProjectInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String contentType = request.getHeader("Content-Type");
        HandlerMethod hm = (HandlerMethod)handler;
        //        System.out.println("preHandle..."+contentType);
//        System.out.println("raw" + readRaw(request));
//        System.out.println(request.getRequestURI());
//        System.out.println(request.getRequestURL());
//        System.out.println("preHandle...");
        return true;

    }

//    /**
//     * 获取request的raw里的数据
//     * @param request
//     * @return
//     */
//    public String readRaw(HttpServletRequest request) {
//        String result = "";
//        InputStream inputStream = null;
//        ByteArrayOutputStream outSteam = null;
//        try {
//            inputStream = request.getInputStream();
//            outSteam = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int len;
//            while ((len = inputStream.read(buffer)) != -1) {
//                outSteam.write(buffer, 0, len);
//            }
//            result = new String(outSteam.toByteArray(), "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                outSteam.close();
//                inputStream.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return result;
//    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println("postHandle...");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("afterCompletion...");
    }
}
