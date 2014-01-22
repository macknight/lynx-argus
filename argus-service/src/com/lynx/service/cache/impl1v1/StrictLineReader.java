package com.lynx.service.cache.impl1v1;

import com.lynx.lib.util.FileUtil;

import java.io.*;
import java.nio.charset.Charset;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-17 上午10:17
 */
public class StrictLineReader implements Closeable {

	private static final byte CR = (byte) '\r';
	private static final byte LF = (byte) '\n';

	private final InputStream instream;
	private final Charset charset;

	private byte[] buf;
	private int pos;
	private int end;

	public StrictLineReader(InputStream instream, Charset charset) {
		this(instream, 8192, charset);
	}

	public StrictLineReader(InputStream instream, int capacity, Charset charset) {
		if (instream == null || charset == null) {
			throw new NullPointerException();
		}
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity <=0");
		}
		if (!(charset.equals(FileUtil.US_ASCII))) {
			throw new IllegalArgumentException("Unsupported encoding");
		}

		this.instream = instream;
		this.charset = charset;
		buf = new byte[capacity];
	}

	@Override
	public void close() throws IOException {
		synchronized (instream) {
			if (buf != null) {
				buf = null;
				instream.close();
			}
		}
	}

	public String readLine() throws IOException {
		synchronized (instream) {
			if (buf == null) {
				throw new IOException("line reader is closed");
			}

			if (pos >= end) {
				fillBuf();
			}

			for (int i = pos; i != end; ++i) {
				if (buf[i] == LF) {
					int lineEnd = (i != pos && buf[i - 1] == CR) ? i - 1 : i;
					String str = new String(buf, pos, lineEnd - pos,
							charset.name());
					pos = i + 1;
					return str;
				}
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream(end - pos
					+ 80) {
				@Override
				public String toString() {
					int length = (count > 0 && buf[count - 1] == CR) ? count - 1
							: count;
					try {
						return new String(buf, 0, length, charset.name());
					} catch (UnsupportedEncodingException e) {
						throw new AssertionError(e);
					}
				}
			};

			while (true) {
				out.write(buf, pos, end - pos);
				end = -1;
				fillBuf();
				for (int i = pos; i != end; ++i) {
					if (buf[i] == LF) {
						if (i != pos) {
							out.write(buf, pos, i - pos);
						}
						pos = i + 1;
						return out.toString();
					}
				}
			}
		}
	}

	private void fillBuf() throws IOException {
		int result = instream.read(buf, 0, buf.length);
		if (result == -1) {
			throw new EOFException();
		}
		pos = 0;
		end = result;
	}
}
