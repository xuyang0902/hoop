package com.tongbanjie.console.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "error")
public class BaseErrorController implements ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(BaseErrorController.class);

    @Override
    public String getErrorPath() {
        logger.warn(">> 进入到错误控制器啦。");
        return "error";
    }

    @RequestMapping
    public String error() {
        return getErrorPath();
    }

}