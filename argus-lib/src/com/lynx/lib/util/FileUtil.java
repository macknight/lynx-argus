package com.lynx.lib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;
import com.lynx.lib.core.LFApplication;

import java.io.*;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * @author zhufeng.liu
 * @version 13-9-17 上午11:39
 */
public class FileUtil {
	public static final Charset US_ASCII = Charset.forName("US-ASCII");
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	private FileUtil() {
		throw new AssertionError("FileUtil shouldn't be instanced");
	}

	public static String readFully(Reader reader) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			char[] buffer = new char[1024];
			int count;
			while ((count = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, count);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}

	/**
	 * Deletes the contents of dir. Throws an IOException if any file could not be deleted, or if dir is not a readable directory.
	 */
	public static void deleteFile(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files == null) {
					throw new IOException("not a readable directory: " + file);
				}
				for (File f : files) {
					if (f.isDirectory()) {
						deleteFile(f);
					} else {
						f.delete();
					}
				}
			}
			file.delete();
		}
	}

	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (RuntimeException rethrown) {
				throw rethrown;
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * 将数据存储到sdcard上
	 * 
	 * @param file
	 * @param data
	 * @return
	 */
	public static boolean saveFile(File file, Object data) {
		if (data instanceof Bitmap) {
			saveBitmap(file, (Bitmap) data);
		} else if (data instanceof byte[]) {
			saveByteArray(file, (byte[]) data);
		}

		return false;
	}

	public static boolean saveBitmap(File file, Bitmap data) {
		if (file == null || data == null)
			return false;
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			data.compress(CompressFormat.PNG, 100, out);
			out.flush();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {

			}
		}
	}

	public static byte[] stream2byte(InputStream instream) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = instream.read(buff, 0, 100)) > 0) {
			baos.write(buff, 0, rc);
		}
		return baos.toByteArray();
	}

	private static boolean saveByteArray(File file, byte[] data) {
		FileOutputStream fos = null;
		try {
			String parent = file.getParent();
			File dir = new File(parent);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			if (file.exists()) {
				file.delete();
			}

			fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
			fos = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return false;
	}

	public static boolean verifyDexFile(File file) {
		try {
			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry je = (JarEntry) entries.nextElement();
				if (!je.getName().equals("classes.dex"))
					continue;

				byte[] readBuffer = new byte[8192];
				// We must read the stream for the JarEntry to retrieve
				// its certificates.
				InputStream is = jarFile.getInputStream(je);
				while (is.read(readBuffer, 0, readBuffer.length) != -1) {
					// not using
				}
				is.close();

				for (Certificate cert : je.getCertificates()) {
					String hash = StringUtil.byteArrayToHexString(cert.getEncoded());
					final int releaseHash = 0xac6fc3fe;
					if (hash.hashCode() == releaseHash)
						return true;
				}
			}
		} catch (Exception e) {
			Log.w("FileUtil", "fail to verify " + file, e);
		}
		return false;
	}

	/**
	 * Reads a file from internal storage
	 * 
	 * @param filename
	 *            - the filename to read from
	 * @return the file contents
	 */
	public static byte[] readExternallStoragePublic(Context context, String filename) {
		int len = 1024;
		byte[] buffer = new byte[len];
		String path = LFApplication.instance().baseExtFileDir();

		if (!isExternalStorageReadOnly()) {
			try {
				File file = new File(path, filename);
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int nrb = fis.read(buffer, 0, len); // read up to len bytes
				while (nrb != -1) {
					baos.write(buffer, 0, nrb);
					nrb = fis.read(buffer, 0, len);
				}
				buffer = baos.toByteArray();
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return buffer;
	}

	public static void writeToExternalStoragePublic(Context context, String filename, byte[] content) {
		// API Level 7 or lower, use getExternalStorageDirectory()
		// to open a File that represents the root of the external
		// storage, but writing to root is not recommended, and instead
		// application should write to application-specific directory, as shown below.
		String path = LFApplication.instance().baseExtFileDir();

		if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
			try {
				File file = new File(path, filename);
				FileOutputStream fos = new FileOutputStream(file, true);
				fos.write(content);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isExternalStorageAvailable() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
			state = true;
		}
		return state;
	}

	public static boolean isExternalStorageReadOnly() {
		boolean state = false;
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
			state = true;
		}
		return state;
	}
}
