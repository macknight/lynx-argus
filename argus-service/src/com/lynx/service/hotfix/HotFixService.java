package com.lynx.service.hotfix;

import com.lynx.lib.core.dex.DexService;

/**
 * 线上bug fix入口
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午1:34
 */
public interface HotFixService extends DexService {

	public void executeOnce();

	public void execute();
}
