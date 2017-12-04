package com.web.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by lenovo on 2017/12/1.
 */
@Controller
@RequestMapping("/test/dd")
public class TestController {
    @RequestMapping("test.do")
    public void testOne(HttpServletResponse response){
        outputData(response,"测试test成功");
        System.out.println("------------test.do-----------");
    }
    @RequestMapping("testTwo.do")
    public void testTwo(HttpServletResponse response){
        outputData(response,"testTwo成功");
        System.out.println("------------testTwo.do-----------");
    }
    @RequestMapping("testThree.do")
    public void testThree(HttpServletResponse response){
        outputData(response,"testThree成功");
        System.out.println("------------testThree.do-----------");
    }
    public void outputData(HttpServletResponse response,String msg){
        response.setContentType("text/html;charset=utf-8");
        try {
            response.getWriter().print(msg);
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
