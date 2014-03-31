package com.lynx.service.cache.impl1v2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author zhufeng.liu
 * @version 14-2-22 上午10:02
 */
public class CacheUtils {

	int fileSize = 10240;

	public CacheEntry readFile(String file) {
		try {
			FileChannel fc = new FileInputStream(file).getChannel();
			CacheEntry entry = new CacheEntry(file, fileSize);
			fc.read(entry.content);
			entry.content.flip();
			entry.pins++;
			return entry;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public synchronized CacheEntry getCacheEntry(Queue<CacheEntry> fileQ, String filename,
			ReentrantLock qLock) {
		qLock.lock();
		Iterator it = fileQ.iterator();

		while (it.hasNext()) {
			CacheEntry entry = (CacheEntry) it.next();
			if (entry.getFileName() == filename) {
				qLock.unlock();
				return entry;
			}
		}
		qLock.unlock();
		return null;
	}

	public synchronized void removeCacheEntryByLRU(Queue<CacheEntry> fileQ, ReentrantLock qLock) {
		qLock.lock();
		fileQ.add(fileQ.remove());
		qLock.unlock();
	}

	public synchronized void updateCacheEntryByLRU(Queue<CacheEntry> fileQ, String filename,
			ReentrantLock qLock) {
		CacheEntry entry = getCacheEntry(fileQ, filename, qLock);
		if (entry == null)
			return;
		qLock.lock();
		fileQ.remove(entry);
		fileQ.add(entry);
		qLock.unlock();
	}
}
