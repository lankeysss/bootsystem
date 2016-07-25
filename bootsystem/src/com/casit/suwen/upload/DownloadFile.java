package com.casit.suwen.upload;
 
public class DownloadFile 
{
	SmartUpload sm=null;
	public DownloadFile()
	{}
	public DownloadFile(SmartUpload sm)
	{
		this.sm=sm;
	}
	  public void downloadFile(String filepath)
	    throws  Exception
	  {
	   downloadFile(filepath, null, null);
	  }

	  public void downloadFile(String filepath, String filenewname)
	    throws Exception
	  {
	    downloadFile(filepath, null, filenewname);
	  }

	 protected   void downloadFile(String paramString1, String paramString2, String paramString3)
	    throws Exception
	  {
	    downloadFile(paramString1, paramString2, paramString3, 65000);
	  }

	  public void downloadFile(String filepath, String ContentType, String filenewname, int maxtemp)
	    throws Exception
	  {
		  sm.downloadFile(filepath, ContentType, filenewname, maxtemp);
	  }
}
