package com.avondrix.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

    @GetMapping("login")
    public String login() {
        return "login";
    }

    @GetMapping("courses")
    public String courses() {
        return "courses";
    }
}
