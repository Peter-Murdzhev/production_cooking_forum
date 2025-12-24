package com.example.cooking_forum;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardController {
//    this throws error 404 on some routes
//    @RequestMapping(value = "/{path:[^\\.]*}")
    // this works for all paths after setup in security config and
    //application.properties - the last line
    @RequestMapping(value = {"/{path:[^\\.]*}", "/**/{path:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}