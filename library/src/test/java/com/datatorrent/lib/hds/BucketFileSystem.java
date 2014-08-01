/*
 *  Copyright (c) 2012-2013 DataTorrent, Inc.
 *  All Rights Reserved.
 */
package com.datatorrent.lib.hds;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.datatorrent.lib.hds.HDS.DataKey;

/**
 * Encapsulate management of meta information and underlying file system interaction.
 * <p>
 * The state of the object will be serialized as part of check-pointing, providing an opportunity to persist meta information and reach consistent state.
 */
public interface BucketFileSystem extends Closeable
{

  public class BucketFileMeta
  {
    String name;
    int size;
    long fromSeq;
    long toSeq;
  }

  /**
   * Performs setup operations eg. crate database connections, delete events of windows greater than last committed
   * window, etc.
   */
  void init();

  /**
   * Create a new empty file for the given key.
   * @param key
   * @param minTime
   * @param maxTime
   * @return
   * @throws IOException
   */
  void createFile(long bucketKey, BucketFileMeta fileMeta) throws IOException;

  DataOutputStream getOutputStream(DataKey key, String fileName) throws IOException;
  DataInputStream getInputStream(DataKey key, BucketFileMeta name) throws IOException;

}
