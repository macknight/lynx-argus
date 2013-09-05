package com.lynx.service.test.impl;

import com.lynx.service.test.TestService;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-5 下午6:00
 */
public class TestServiceImpl implements TestService{

    @Override
    public String hello(String name) {
        return "hello world " + name;
    }
}
