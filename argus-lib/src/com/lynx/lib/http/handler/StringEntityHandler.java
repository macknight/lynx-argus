package com.lynx.lib.http.handler;

import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 2013-4-17 下午10:51:03
 */
public class StringEntityHandler {
	public Object handleEntity(HttpEntity entity, EntityCallback callback,
			String charset) throws IOException {
		if (entity == null)
			return null;

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];

		long count = entity.getContentLength();
		long curCount = 0;
		int len = -1;
		InputStream is = entity.getContent();
		while ((len = is.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
			curCount += len;
			if (callback != null)
				callback.callback(count, curCount, false);
		}
		if (callback != null)
			callback.callback(count, curCount, true);
		byte[] data = outStream.toByteArray();
		outStream.close();
		is.close();
		return new String(data, charset);
	}
}
