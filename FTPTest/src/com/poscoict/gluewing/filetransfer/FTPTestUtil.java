package com.poscoict.gluewing.filetransfer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class FTPTestUtil {
	public static void main(String[] args) {
		Properties prop = new Properties();

		prop.setProperty(FTPConstants.FTP_URL, "172.31.7.74");
		prop.setProperty(FTPConstants.FTP_PORT, "21");
		prop.setProperty(FTPConstants.FTP_DEF_DIR, "/InquiryAttaches/MES/SCOBULLETIN1455728752670");
		prop.setProperty(FTPConstants.FTP_USER_NAME, "FTPMESUSER");
		prop.setProperty(FTPConstants.FTP_PASSWORD, "mes123$");
		prop.setProperty(FTPConstants.FTP_SYSTEM, "WINDOWS");

		FTPConnection ftpclient = new FTPConnection(prop);

		try {
			ftpclient.connect();
			Boolean existDir = ftpclient.checkDirectoryExists(prop.getProperty(FTPConstants.FTP_DEF_DIR));

			if (existDir) {
				System.out.println("Exists Dir : " + ftpclient.getCurrentDirectory());
			} else {
				ftpclient.makeDirectories(prop.getProperty(FTPConstants.FTP_DEF_DIR));
			}

			// UpLoad
			// 1. File
			// ftpclient.uploadFile(file, new File("C:\\WAS\\AAA1.xls"));

			// 2. Byte[]
			// FileInputStream fis = new FileInputStream(file);
			// ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// byte[] buf = new byte[1024];
			// try {
			// for (int readNum; (readNum = fis.read(buf)) != -1;) {
			// bos.write(buf, 0, readNum);
			// }
			// } catch (IOException ex) {
			// }
			// byte[] bytes = bos.toByteArray();
			//
			// ftpclient.uploadFile(bytes, new File("AAA2.xls"));

			// Download
			// 1. Download
//			File downloadfile = ftpclient.downloadFile(prop.getProperty(FTPConstants.FTP_DEF_DIR) + "/AAA2.xls", "TestData.xls");
//
//			if (downloadfile.exists() && downloadfile.isFile()) {
//				System.out.println("File Name : [ " + downloadfile + " ]");
//				System.out.println("File exists : [ " + downloadfile.exists() + " ]");
//				System.out.println("File isFile : [ " + downloadfile.isFile() + " ]");
//				System.out.println("File getName : [ " + downloadfile.getName() + " ]");
//				System.out.println("File getPath : [ " + downloadfile.getPath() + " ]");
//				System.out.println("File length : [ " + downloadfile.length() + " ]");
//			}
			
			ftpclient.removeFile(prop.getProperty(FTPConstants.FTP_DEF_DIR) + "/SCOBULLETIN1455728752670003.xls");
			
//			String aaa = lpad("12", 3, '0');
			
//			System.out.println(aaa);
			
			
			
			ftpclient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static String lpad(String str, int length, char fillChar) {
		if (str.length() > length) return str;
		char[] chars = new char[length];
		Arrays.fill(chars, fillChar);
		System.arraycopy(str.toCharArray(), 0, chars, length - str.length(), str.length());
		return new String(chars);
	}

}
