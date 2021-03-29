package com.hjc.main.controller;

import com.hjc.springframework.annotation.Autowired;
import com.hjc.springframework.annotation.Controller;
import com.hjc.springframework.annotation.RequestMapping;
import com.hjc.main.service.HelloService;
import com.hjc.springframework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author kei
 */
@Controller
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("/hello1")
    public void hello(HttpServletRequest request, HttpServletResponse response, @RequestParam("param") String param) {
        String msg = helloService.getMsg();
        try {
            response.getWriter().write(msg);
            response.getWriter().write("\n");
            response.getWriter().write(param);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
