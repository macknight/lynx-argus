package com.lynx.service.cache.impl1v2;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 14-2-22 下午10:04
 */
public class Cache extends FileCache {

	private int numEntries;
	Set<String> fileSet;
	Queue<CacheEntry> fileQ;
	int fileSize;
	CacheUtils utils;
	ReentrantLock setLock;
	ReentrantLock qLock;
	ReentrantLock mutex;

	protected Cache(int maxCacheEntries) {
		super(maxCacheEntries);
		numEntries = 0;
		fileSet = new HashSet<String>();
		fileQ = new LinkedList<CacheEntry>();
		fileSize = 10240;
		utils = new CacheUtils();
		setLock = new ReentrantLock();
		qLock = new ReentrantLock();
		mutex = new ReentrantLock();
	}

	public void pinFiles(Collection<String> fileNames) {
		for (String file : fileNames) {
			System.out.println("Filename to pin : " + file);
			if (fileSet.contains(file)) {
				System.out.println(file + " already pinned.");
				CacheEntry entry = utils.getCacheEntry(fileQ, file, qLock);
				entry.setPinCount(entry.getPinCount() + 1);
				utils.updateCacheEntryByLRU(fileQ, file, qLock);
				continue;
			} else {
				if (fileSet.size() < maxCacheEntries) {
					System.out.println("Not pinned already. Pinning " + file + " now");
					CacheEntry entry = utils.readFile(file);
					if (entry != null) {
						qLock.lock();
						fileQ.add(entry);
						qLock.unlock();
						setLock.lock();
						fileSet.add(file);
						setLock.unlock();
					}
				} else {
					setLock.lock();
					fileSet.remove(fileQ.peek().getFileName());
					setLock.unlock();
					utils.removeCacheEntryByLRU(fileQ, qLock);
					CacheEntry entry = utils.readFile(file);
					if (entry != null) {
						qLock.lock();
						fileQ.add(entry);
						qLock.unlock();
						setLock.lock();
						fileSet.add(file);
						setLock.unlock();
					}
				}
			}
		}
	}

	public void unpinFiles(Collection<String> fileNames) {
		for (String file : fileNames) {
			System.out.println("Filename to unpin : " + file);
			if (fileSet.contains(file)) {
				CacheEntry entry = utils.getCacheEntry(fileQ, file, qLock);
				entry.lock();
				entry.setPinCount(entry.getPinCount() - 1);
				if (entry.getPinCount() == 0) {
					System.out.println("Pin count = 0; Unpinning the file from cache...");
					qLock.lock();
					fileQ.remove(entry);
					qLock.unlock();
					setLock.lock();
					fileSet.remove(file);
					setLock.unlock();
				} else {
					System.out.println("Pin count not zero. File Still in cache.");
				}
				entry.unlock();
			} else {
				System.out.println("File " + file + " is not pinned. Can't unpin file!");
			}
		}
	}

	@Override
	ByteBuffer fileData(String fileName) {
		if (!fileSet.contains(fileName)) {
			return null;
		}
		CacheEntry entry = utils.getCacheEntry(fileQ, fileName, qLock);
		if (entry == null) {
			return null;
		}
		return entry.getContent().asReadOnlyBuffer();
	}

	@Override
	ByteBuffer mutableFileData(String fileName) {
		if (!fileSet.contains(fileName)) {
			return null;
		}
		CacheEntry entry = utils.getCacheEntry(fileQ, fileName, qLock);
		if (entry == null) {
			return null;
		}
		entry.makeDirty();
		return entry.getContent();
	}

	@Override
	void shutdown() {
		// TODO Auto-generated method stub

	}

}
