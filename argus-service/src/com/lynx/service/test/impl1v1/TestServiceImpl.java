package com.lynx.service.test.impl1v1;

import com.lynx.service.test.TestService;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-5 下午6:13
 */
public class TestServiceImpl implements TestService {

	public static final String Tag = "test";

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public String hello(String name) {
		return name + " hello world";
	}
}
