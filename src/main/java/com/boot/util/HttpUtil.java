package com.boot.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class HttpUtil {

    public static String getSessionIdFromReq(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase("session")) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }
}
