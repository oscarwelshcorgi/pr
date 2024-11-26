package com.pr.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

public class UtilController {
/*
    @RequestMapping("/sitemap.xml")
    public String showSitemap() {
        return "redirect:sitemap.xml";
    }
*/
    @RequestMapping(value = { "/Robots.txt", "/Robots.txt", "/robots.txt" })
    public void robots(
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.getWriter().write("User-agent: *\nAllow: /\n#DaumWebMasterTool:b95a0c409465e0987709a93397179e195281ffafd100375360aa073492c8856d:IXoE9Sbxr5AafkHJRCBgzw==");
    }

    @RequestMapping("/ads.txt")
    public String Ads() {
        return "redirect:ads.txt";
    }

}
