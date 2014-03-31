package com.lynx.service.cache.impl1v2;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author zhufeng.liu
 * @version 14-2-22 下午3:36
 */
public class CacheEntry {
	String fileName;
	int pins;
	ByteBuffer content;

	ReentrantLock lock;
	ReentrantReadWriteLock readWriteLock;
	Lock read;
	Lock write;
	boolean dirty;

	CacheEntry(String name, int size) {
		fileName = name;
		content = ByteBuffer.allocate(size);
		pins = 0;
		lock = new ReentrantLock();
		readWriteLock = new ReentrantReadWriteLock();
		read = readWriteLock.readLock();
		write = readWriteLock.writeLock();
		dirty = false;
	}

	void makeDirty() {
		lock();
		dirty = true;
		unlock();
	}

	int getPinCount() {
		lock();
		int pinCount = pins;
		unlock();
		return pinCount;
	}

	void setPinCount(int count) {
		lock();
		pins = count;
		unlock();
	}

	String getFileName() {
		return fileName;
	}

	ByteBuffer getContent() {
		return content;
	}

	void lock() {
		lock.lock();
	}

	void unlock() {
		lock.unlock();
	}
}
