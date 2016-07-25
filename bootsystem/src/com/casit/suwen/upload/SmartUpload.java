package com.casit.suwen.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream; 
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 
import javax.servlet.jsp.PageContext;

import com.casit.suwen.security.FileSave;


@SuppressWarnings("unchecked")
public class SmartUpload
{
  protected byte[] m_binArray;
  protected HttpServletRequest m_request;
  protected HttpServletResponse m_response;
  protected ServletContext m_application;
  private int m_totalBytes = 0;
  private int m_currentIndex = 0;
  private int m_startData = 0;
  private int m_endData = 0;
  private String m_boundary = new String();
  private long m_totalMaxFileSize = 0L;
  private long m_maxFileSize = 0L;
  @SuppressWarnings("rawtypes")
  private Vector m_deniedFilesList = new Vector();
  @SuppressWarnings("rawtypes")
  private Vector m_allowedFilesList = new Vector();
  private boolean m_denyPhysicalPath = false;
  //private boolean m_forcePhysicalPath = false;
  private String m_contentDisposition = new String();
  public static final int SAVE_AUTO = 0;
  public static final int SAVE_VIRTUAL = 1;
  public static final int SAVE_PHYSICAL = 2;
  private UploadFiles m_files = new UploadFiles();
  private String chartName="UTF-8";  
  private String headchar="ISO_8859_1";


  private Request m_formRequest = new Request();

  /** @deprecated */
  public final void init(ServletConfig paramServletConfig)
    throws ServletException
  {
    this.m_application = paramServletConfig.getServletContext();
  }

  /** @deprecated */
  public void service(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    this.m_request = paramHttpServletRequest;
    this.m_response = paramHttpServletResponse;
  }

  public final void initialize(ServletConfig paramServletConfig, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException
  {
    this.m_application = paramServletConfig.getServletContext();
    this.m_request = paramHttpServletRequest;
    this.m_response = paramHttpServletResponse;
  }

  public final void initialize(PageContext paramPageContext)
    throws ServletException
  {
    this.m_application = paramPageContext.getServletContext();
    this.m_request = ((HttpServletRequest)paramPageContext.getRequest());
    this.m_response = ((HttpServletResponse)paramPageContext.getResponse());
  }

  /** */
  public final void initialize(ServletContext paramServletContext , HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse )
    throws ServletException
  {
    this.m_application = paramServletContext;
    this.m_request = paramHttpServletRequest;
    this.m_response = paramHttpServletResponse;
  }

  public void upload()
    throws ServletException, IOException, UploadException
  {
    int i = 0;
    int j = 0;
    long l = 0L;
    int k = 0;
    String str1 = new String();
    String str2 = new String();
    String str3 = new String();
    String str4 = new String();
    String str5 = new String();
    String str6 = new String();
    String str7 = new String();
    String str8 = new String();
    String str9 = new String();
    int m = 0;

    this.m_totalBytes = this.m_request.getContentLength();

    this.m_binArray = new byte[this.m_totalBytes];

    while (i < this.m_totalBytes) {
      try {
        this.m_request.getInputStream();
        j = this.m_request.getInputStream().read(this.m_binArray, i, this.m_totalBytes - i);
      }
      catch (Exception localException)
      {
        throw new UploadException("Unable to upload.");
      }
      i += j;
    }

    while ((k == 0) && (this.m_currentIndex < this.m_totalBytes)) {
      if (this.m_binArray[this.m_currentIndex] == 13)
        k = 1;
      else
        this.m_boundary += (char)this.m_binArray[this.m_currentIndex];
      this.m_currentIndex += 1;
    }

    if (this.m_currentIndex == 1) {
      return;
    }

    this.m_currentIndex += 1;

    while (this.m_currentIndex < this.m_totalBytes)
    {
      str1 = getDataHeader();

      this.m_currentIndex += 2;

      m = str1.indexOf("filename") > 0 ? 1 : 0;

      str2 = getDataFieldValue(str1, "name");

      if (m != 0) {
        str5 = getDataFieldValue(str1, "filename");
        str3 = getFileName(str5);
        str4 = getFileExt(str3);
        str6 = getContentType(str1);
        str7 = getContentDisp(str1);
        str8 = getTypeMIME(str6);
        str9 = getSubTypeMIME(str6);
      }

      getDataSection();

      if (m != 0)
      {
        if (str3.length() > 0)
        {
          if (this.m_deniedFilesList.contains(str4) == true) {
            throw new SecurityException("The extension of the file is denied to be uploaded (1015).");
          }

          if ((!this.m_allowedFilesList.isEmpty()) && (!this.m_allowedFilesList.contains(str4)))
          {
            throw new SecurityException("The extension of the file is not allowed to be uploaded (1010).");
          }

          if ((this.m_maxFileSize > 0L) && (this.m_endData - this.m_startData + 1 > this.m_maxFileSize))
          {
            throw new SecurityException("Size exceeded for this file : " + str3 + " (1105).");
          }

          l += this.m_endData - this.m_startData + 1;
          if ((this.m_totalMaxFileSize > 0L) && (l > this.m_totalMaxFileSize))
          {
            throw new SecurityException("Total File Size exceeded (1110).");
          }
        }
      }
      Object localObject;
      if (m != 0)
      {
        localObject = new UploadFile();

        ((UploadFile)localObject).setParent(this);
        ((UploadFile)localObject).setFieldName(str2);
        ((UploadFile)localObject).setFileName(str3);
        ((UploadFile)localObject).setFileExt(str4);
        ((UploadFile)localObject).setFilePathName(str5);
        ((UploadFile)localObject).setIsMissing(str5.length() == 0);
        ((UploadFile)localObject).setContentType(str6);
        ((UploadFile)localObject).setContentDisp(str7);
        ((UploadFile)localObject).setTypeMIME(str8);
        ((UploadFile)localObject).setSubTypeMIME(str9);

        if (str6.indexOf("application/x-macbinary") > 0) {
          this.m_startData += 128;
        }
        ((UploadFile)localObject).setSize(this.m_endData - this.m_startData + 1);
        ((UploadFile)localObject).setStartData(this.m_startData);
        ((UploadFile)localObject).setEndData(this.m_endData);

        this.m_files.addFile((UploadFile)localObject);
      }
      else
      {
        localObject = new String(this.m_binArray, this.m_startData, this.m_endData - this.m_startData + 1,chartName);

        this.m_formRequest.putParameter(str2, (String)localObject);
      }

      if ((char)this.m_binArray[(this.m_currentIndex + 1)] == '-') {
        break;
      }
      this.m_currentIndex += 2;
    }
  }

  public int save(String paramString)
    throws ServletException, IOException, UploadException
  {
    return save(paramString, 1);
  }

  protected int save(String paramString, int paramInt)
    throws ServletException, IOException, UploadException
  {
    int i = 0;

    if (paramString == null) {
      paramString = this.m_application.getRealPath("/");
    }

    if (paramString.indexOf("/") != -1) {
      if (paramString.charAt(paramString.length() - 1) != '/')
        paramString = paramString + "/";
    }
    else if (paramString.charAt(paramString.length() - 1) != '\\') {
      paramString = paramString + "\\";
    }
    for (int j = 0; j < this.m_files.getCount(); j++) {
      if (!this.m_files.getFile(j).isMissing()) {
        this.m_files.getFile(j).saveAs(paramString + this.m_files.getFile(j).getFileName(), paramInt);

        i++;
      }
    }
    return i;
  }

  public int getSize()
  {
    return this.m_totalBytes;
  }

  public byte getBinaryData(int paramInt)
  {
    int i;
    try
    {
      i = this.m_binArray[paramInt];
    } catch (Exception localException) {
      throw new ArrayIndexOutOfBoundsException("Index out of range (1005).");
    }

    return (byte) i;
  }

  public UploadFiles getFiles()
  {
    return this.m_files;
  }

  public Request getRequest()
  {
    return this.m_formRequest;
  }

  public void downloadFile(String paramString)
    throws ServletException, IOException, UploadException
  {
    downloadFile(paramString, null, null);
  }

  public void downloadFile(String paramString1, String paramString2)
    throws ServletException, IOException, UploadException, UploadException
  {
    downloadFile(paramString1, paramString2, null);
  }

  public void downloadFile(String paramString1, String paramString2, String paramString3)
    throws ServletException, IOException, UploadException
  {
    downloadFile(paramString1, paramString2, paramString3, 65000);
  }

  public void downloadFile(String paramString1, String paramString2, String paramString3, int paramInt)
    throws ServletException, IOException, UploadException
  {
    if (paramString1 == null) throw new IllegalArgumentException("File '" + paramString1 + "' not found (1040).");

    if (paramString1.equals("")) throw new IllegalArgumentException("File '" + paramString1 + "' not found (1040).");

    if ((!isVirtual(paramString1)) && (this.m_denyPhysicalPath)) {
      throw new SecurityException("Physical path is denied (1035).");
    }

    if (isVirtual(paramString1)) paramString1 = this.m_application.getRealPath(paramString1);

    java.io.File localFile = new java.io.File(paramString1);
    FileInputStream localFileInputStream = new FileInputStream(localFile);

    long l = localFile.length();
    int i = 0;
    int j = 0;
    byte[] arrayOfByte = new byte[paramInt];

    if (paramString2 == null)
      this.m_response.setContentType("application/x-msdownload");
    else if (paramString2.length() == 0)
      this.m_response.setContentType("application/x-msdownload");
    else {
      this.m_response.setContentType(paramString2);
    }

    this.m_response.setContentLength((int)l);

    this.m_contentDisposition = "attachment;";
    	//(this.m_contentDisposition == null ? "attachment;" : this.m_contentDisposition);

    if (paramString3 == null) this.m_response.setHeader("Content-Disposition", this.m_contentDisposition + " filename=" + new String(getFileName_char(paramString1).getBytes(),headchar));
    else if (paramString3.length() == 0)
      this.m_response.setHeader("Content-Disposition", this.m_contentDisposition);
    else {
      this.m_response.setHeader("Content-Disposition", this.m_contentDisposition + " filename=" + new String(paramString3.getBytes(),headchar) );
    }

    while (j < l) {
      i = localFileInputStream.read(arrayOfByte, 0, paramInt);
      j += i;
      this.m_response.getOutputStream().write(arrayOfByte, 0, i);
    }
    localFileInputStream.close();
  }

  public void downloadField(ResultSet paramResultSet, String paramString1, String paramString2, String paramString3)
    throws ServletException, IOException, SQLException
  {
    if (paramResultSet == null) throw new IllegalArgumentException("The RecordSet cannot be null (1045).");

    if (paramString1 == null) throw new IllegalArgumentException("The columnName cannot be null (1050).");

    if (paramString1.length() == 0) throw new IllegalArgumentException("The columnName cannot be empty (1055).");

    byte[] arrayOfByte = paramResultSet.getBytes(paramString1);

    if (paramString2 == null)
      this.m_response.setContentType("application/x-msdownload");
    else if (paramString2.length() == 0)
      this.m_response.setContentType("application/x-msdownload");
    else {
      this.m_response.setContentType(paramString2);
    }

    this.m_response.setContentLength(arrayOfByte.length);

    if (paramString3 == null)
      this.m_response.setHeader("Content-Disposition", "attachment;");
    else if (paramString3.length() == 0)
      this.m_response.setHeader("Content-Disposition", "attachment;");
    else {
      this.m_response.setHeader("Content-Disposition", "attachment; filename=" + paramString3);
    }

    this.m_response.getOutputStream().write(arrayOfByte, 0, arrayOfByte.length);
  }

  public void fieldToFile(ResultSet paramResultSet, String paramString1, String paramString2)
    throws ServletException, IOException, UploadException, SQLException
  {
    try
    {
      if (this.m_application.getRealPath(paramString2) != null) {
        paramString2 = this.m_application.getRealPath(paramString2);
      }

      InputStream localInputStream = paramResultSet.getBinaryStream(paramString1);

      FileOutputStream localFileOutputStream = new FileOutputStream(paramString2);
      int i;
      while ((i = localInputStream.read()) != -1)
        localFileOutputStream.write(i);
      localFileOutputStream.close();
    } catch (Exception localException) {
      throw new UploadException("Unable to save file from the DataBase (1020).");
    }
  }

  private String getDataFieldValue(String paramString1, String paramString2)
  {
    String str1 = new String();
    String str2 = new String();
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;

    str1 = paramString2 + "=" + '"';
    i = paramString1.indexOf(str1);

    if (i > 0) {
      j = i + str1.length();
      k = j;
      str1 = "\"";
      m = paramString1.indexOf(str1, j);
      if ((k > 0) && (m > 0)) str2 = paramString1.substring(k, m);
    }
    return str2;
  }

  private String getFileExt(String paramString)
  {
    String str = new String();
    int i = 0;
    int j = 0;

    if (paramString == null) {
      return null;
    }

    i = paramString.lastIndexOf('.') + 1;
    j = paramString.length();
    str = paramString.substring(i, j);
    if (paramString.lastIndexOf('.') > 0) {
      return str;
    }
    return "";
  }

  private String getContentType(String paramString)
  {
    String str1 = new String();
    String str2 = new String();
    int i = 0;
    int j = 0;

    str1 = "Content-Type:";
    i = paramString.indexOf(str1) + str1.length();

    if (i != -1) {
      j = paramString.length();
      str2 = paramString.substring(i, j);
    }
    return str2;
  }

  private String getTypeMIME(String paramString)
  { 
    int i = 0;

    i = paramString.indexOf("/");

    if (i != -1) {
      return paramString.substring(1, i);
    }
    return paramString;
  }

  private String getSubTypeMIME(String paramString)
  { 
    int i = 0;
    int j = 0;

    i = paramString.indexOf("/") + 1;
    if (i != -1) {
      j = paramString.length();
      return paramString.substring(i, j);
    }
    return paramString;
  }

  private String getContentDisp(String paramString)
  {
    String str = new String();
    int i = 0;
    int j = 0;

    i = paramString.indexOf(":") + 1;
    j = paramString.indexOf(";");
    str = paramString.substring(i, j);
    return str;
  }

  private void getDataSection()
  { 
    int j = this.m_currentIndex;
    int k = 0;
    int m = this.m_boundary.length();
    this.m_startData = this.m_currentIndex;
    this.m_endData = 0;

    while (j < this.m_totalBytes)
    {
      if (this.m_binArray[j] == (byte)this.m_boundary.charAt(k))
      {
        if (k == m - 1) {
          this.m_endData = (j - m + 1 - 3);
          break;
        }
        j++;
        k++;
      }
      else {
        j++;
        k = 0;
      }
    }

    this.m_currentIndex = (this.m_endData + m + 3);
  }

  private String getDataHeader()
  {
    int i = this.m_currentIndex;
    int j = 0; 
    int m = 0;

    while (m == 0)
    {
      if ((this.m_binArray[this.m_currentIndex] == 13) && (this.m_binArray[(this.m_currentIndex + 2)] == 13))
      {
        m = 1;
        j = this.m_currentIndex - 1;
        this.m_currentIndex += 2;
      }
      else {
        this.m_currentIndex += 1;
      }
    }
    String str="";
	try {
		str = new String(this.m_binArray, i, (j - i + 1) ,chartName);
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    return str;
  }

  private String getFileName(String paramString)
  { 
	try { 
	    int i = 0;  
	
	    i = paramString.lastIndexOf('/');
	    if (i != -1)
	    	return new String(paramString.substring(i + 1, paramString.length()).getBytes());
	
	    i = paramString.lastIndexOf('\\');
	    if (i != -1) {
	    	return new String(paramString.substring(i + 1, paramString.length()).getBytes());
	    }
	    return new String(paramString.getBytes());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    return paramString;
  }
  
  private String getFileName_char(String paramString)
  { 
	try { 
	    int i = 0; 
	
	    i = paramString.lastIndexOf('/');
	    if (i != -1)
	    	return new String(paramString.substring(i + 1, paramString.length()).getBytes(),"ISO_8859_1");
	
	    i = paramString.lastIndexOf('\\');
	    if (i != -1) {
	    	return new String(paramString.substring(i + 1, paramString.length()).getBytes(),"ISO_8859_1");
	    }
	    return new String(paramString.getBytes(),"ISO_8859_1");
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    return paramString;
  }

  public void setDeniedFilesList(String paramString)
    throws ServletException, IOException, SQLException
  {
    String str = "";

    if (paramString != null) {
      str = "";
      for (int i = 0; i < paramString.length(); i++) {
        if (paramString.charAt(i) == ',') {
          if (!this.m_deniedFilesList.contains(str))
            this.m_deniedFilesList.addElement(str);
          str = "";
        } else {
          str = str + paramString.charAt(i);
        }
      }
      if (str != "") this.m_deniedFilesList.addElement(str); 
    } else {
      this.m_deniedFilesList = null;
    }
  }

  public void setAllowedFilesList(String paramString)
  {
    String str = "";

    if (paramString != null) {
      str = "";
      for (int i = 0; i < paramString.length(); i++) {
        if (paramString.charAt(i) == ',') {
          if (!this.m_allowedFilesList.contains(str))
            this.m_allowedFilesList.addElement(str);
          str = "";
        } else {
          str = str + paramString.charAt(i);
        }
      }
      if (str != "") this.m_allowedFilesList.addElement(str); 
    } else {
      this.m_allowedFilesList = null;
    }
  }

  public void setDenyPhysicalPath(boolean paramBoolean)
  {
    this.m_denyPhysicalPath = paramBoolean;
  }

 

  public void setContentDisposition(String paramString)
  {
    this.m_contentDisposition = paramString;
  }

  public void setTotalMaxFileSize(long paramLong)
  {
    this.m_totalMaxFileSize = paramLong;
  }

  public void setMaxFileSize(long paramLong)
  {
    this.m_maxFileSize = paramLong;
  }

  public String getPhysicalPath(String paramString, int paramInt)
    throws IOException
  {
    String str1 = new String();
    String str2 = new String();
    String str3 = new String();
    int i = 0;
    str3 = System.getProperty("file.separator");

    if (paramString == null) throw new IllegalArgumentException("There is no specified destination file (1140).");

    if (paramString.equals("")) throw new IllegalArgumentException("There is no specified destination file (1140).");

    if (paramString.lastIndexOf("\\") >= 0) {
      str1 = paramString.substring(0, paramString.lastIndexOf("\\"));
      str2 = paramString.substring(paramString.lastIndexOf("\\") + 1);
    }
    if (paramString.lastIndexOf("/") >= 0) {
      str1 = paramString.substring(0, paramString.lastIndexOf("/"));
      str2 = paramString.substring(paramString.lastIndexOf("/") + 1);
    }
    str1 = str1.length() == 0 ? "/" : str1;

    java.io.File localFile = new java.io.File(str1);
    if (localFile.exists()) i = 1;
   

    if (paramInt == 0)
    {
      if (isVirtual(str1)) {
        str1 = this.m_application.getRealPath(str1);
        if (str1.endsWith(str3))
          str1 = str1 + str2;
        else {
          str1 = str1 + str3 + str2;
        }
        return str1;
      }
      if (i != 0) {
        if (this.m_denyPhysicalPath) {
          throw new IllegalArgumentException("Physical path is denied (1125).");
        }

        return paramString;
      }
      else
      {
    	  File file=new File(paramString);
  		  FileSave.creatdict(file);
      }
      
      return paramString;
     // throw new IllegalArgumentException("This path does not exist (1135).");
    }

    if (paramInt == 1) {
      if (isVirtual(str1)) {
        str1 = this.m_application.getRealPath(str1);
        if (str1.endsWith(str3))
          str1 = str1 + str2;
        else {
          str1 = str1 + str3 + str2;
        }
        return str1;
      }
      if (i != 0) {
        throw new IllegalArgumentException("The path is not a virtual path.");
      }

      throw new IllegalArgumentException("This path does not exist (1135).");
    }

    if (paramInt == 2) {
      if (i != 0) {
        if (this.m_denyPhysicalPath) {
          throw new IllegalArgumentException("Physical path is denied (1125).");
        }

        return paramString;
      }

      if (isVirtual(str1)) {
        throw new IllegalArgumentException("The path is not a physical path.");
      }

      throw new IllegalArgumentException("This path does not exist (1135).");
    }

    return null;
  }

  public void uploadInFile(String paramString)
    throws IOException, UploadException
  {
    int i = 0;
    int j = 0;
    int k = 0;

    if (paramString == null) throw new IllegalArgumentException("There is no specified destination file (1025).");

    if (paramString.length() == 0) throw new IllegalArgumentException("There is no specified destination file (1025).");

    if ((!isVirtual(paramString)) && (this.m_denyPhysicalPath)) {
      throw new SecurityException("Physical path is denied (1035).");
    }

    i = this.m_request.getContentLength();

    this.m_binArray = new byte[i];

    while (j < i) {
      try {
        k = this.m_request.getInputStream().read(this.m_binArray, j, i - j);
      }
      catch (Exception localException1) {
        throw new UploadException("Unable to upload.");
      }
      j += k;
    }

    if (isVirtual(paramString)) paramString = this.m_application.getRealPath(paramString);

    try
    {
      java.io.File localFile = new java.io.File(paramString);

      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      localFileOutputStream.write(this.m_binArray);
      localFileOutputStream.close();
    }
    catch (Exception localException2) {
      throw new UploadException("The Form cannot be saved in the specified file (1030).");
    }
  }

  private boolean isVirtual(String paramString)
  {
    if (this.m_application.getRealPath(paramString) != null)
    {
      java.io.File localFile = new java.io.File(this.m_application.getRealPath(paramString));

      return localFile.exists();
    }
    return false;
  }
}