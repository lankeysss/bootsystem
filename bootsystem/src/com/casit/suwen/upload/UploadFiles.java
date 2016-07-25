package com.casit.suwen.upload;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

@SuppressWarnings("unchecked")
public class UploadFiles
{ 
  @SuppressWarnings("rawtypes")
private Hashtable m_files = new Hashtable();
  private int m_counter = 0;

  protected void addFile(UploadFile paramFile)
  {
    if (paramFile == null) {
      throw new IllegalArgumentException("newFile cannot be null.");
    }

    this.m_files.put(new Integer(this.m_counter), paramFile);
    this.m_counter += 1;
  }

  public UploadFile getFile(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("File's index cannot be a negative value (1210).");
    }

    UploadFile localFile = (UploadFile)this.m_files.get(new Integer(paramInt));

    if (localFile == null) {
      throw new IllegalArgumentException("Files' name is invalid or does not exist (1205).");
    }

    return localFile;
  }

  public int getCount()
  {
    return this.m_counter;
  }

  public long getSize()
    throws IOException
  {
    long l = 0L;
    for (int i = 0; i < this.m_counter; i++) {
      l += getFile(i).getSize();
    }
    return l;
  }

  @SuppressWarnings("rawtypes")
public Collection getCollection()
  {
    return this.m_files.values();
  }

  @SuppressWarnings("rawtypes")
public Enumeration getEnumeration()
  {
    return this.m_files.elements();
  }
}