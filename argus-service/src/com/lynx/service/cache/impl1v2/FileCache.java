package com.lynx.service.cache.impl1v2;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-9-17 下午3:36
 */
public abstract class FileCache {
	// Maximum number of files that can be cached at any time.
	protected final int maxCacheEntries;

	/**
	 * Constructor. 'maxCacheEntries' is the maximum number of files that can be cached at any time.
	 * 
	 * @param maxCacheEntries
	 */
	protected FileCache(final int maxCacheEntries) {
		this.maxCacheEntries = maxCacheEntries;
	}

	/**
	 * Pins the given files in vector 'fileNames' in the cache. If any of these files are not already cached, they are
	 * first read from the filesystem. If the cache is full, then some existing cache entries may be evicted. If no
	 * entries can be evicted (e.g., if they are all pinned, or dirty), then this method will block until a suitable
	 * number of cache entries becomes available. It is OK for more than one thread to pin the same file, however the
	 * file should not become unpinned until both pins have been removed.
	 * 
	 * @param fileNames
	 */
	abstract void pinFiles(Collection<String> fileNames);

	/**
	 * Unpin one or more files that were previously pinned. It is ok to unpin only a subset of the files that were
	 * previously pinned using pinFiles(). It is undefined behavior to unpin a file that wasn't pinned.
	 * 
	 * @param fileNames
	 */
	abstract void unpinFiles(Collection<String> fileNames);

	/**
	 * Provide read-only access to a pinned file's data in the cache. This call should never block (other than
	 * temporarily while contending on a lock). It is undefined behavior if the file is not pinned, or to access the
	 * buffer when the file isn't pinned.
	 * 
	 * @param fileName
	 * @return
	 */
	abstract ByteBuffer fileData(String fileName);

	/**
	 * Provide write access to a pinned file's data in the cache. This call marks the file's data as 'dirty'. The caller
	 * may update the contents of the file by writing to the memory pointed by the returned value. This call should
	 * never block (other than temporarily while contending on a lock).
	 * 
	 * Multiple clients may have access to the data, however the cache *does not* have to worry about synchronizing the
	 * clients' accesses (you may assume the application does this correctly). It is undefined behavior if the file is
	 * not pinned, or to access the buffer when the file is not pinned.
	 * 
	 * @param fileName
	 * @return
	 */
	abstract ByteBuffer mutableFileData(String fileName);

	/**
	 * Flushes all dirty buffers. This must be called before removing all references. The cache cannot be used after it
	 * has been shut down.
	 */
	abstract void shutdown();
}
