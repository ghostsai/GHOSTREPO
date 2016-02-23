package com.poscoict.gluewing.filetransfer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPConnection {
	private Properties m_ftpProperty;
	private FTPConnInfo m_connInfo;
	private boolean m_connectFlag;
	private int m_returnCode;

	private FTPClient m_ftpClient;

    
	
	
	
	
	
	
	
	
	/**
	 * @return the m_connectFlag
	 */
	public boolean isConnectFlag() {
		return m_connectFlag;
	}

	/**
	 * @param m_connectFlag
	 *            the m_connectFlag to set
	 */
	public void setConnectFlag(boolean m_connectFlag) {
		this.m_connectFlag = m_connectFlag;
	}

	public FTPConnection(Properties property) {
		this.m_ftpProperty = property;
		this.m_connInfo = new FTPConnInfo(this.m_ftpProperty);
		this.setConnectFlag(false);

		m_ftpClient = new FTPClient();
	}

	/**
	 * Connects to a remote FTP server
	 */
	public void connect() throws Exception {
		try {
			m_ftpClient.connect(this.m_connInfo.getFtpUrl(), this.m_connInfo.getFtpPort());

			// 연결 시도후, 성공했는지 응답 코드 확인
			m_returnCode = m_ftpClient.getReplyCode();

			if (!FTPReply.isPositiveCompletion(m_returnCode)) {
				this.disconnect();
				this.setConnectFlag(false);

				System.out.println("FTP Server Connection Deny");
			} else {
				System.out.println("FTP Server Connect Success!");
				
				Boolean loginFlag = m_ftpClient.login(this.m_connInfo.getUserName(), this.m_connInfo.getPasswd());

				
				if (loginFlag) {
					System.out.println("FTP Server Login Success!");
					this.setConnectFlag(true);
				} else {
					System.out.println("FTP Server Login Failure!");
					
					this.disconnect();
					this.setConnectFlag(false);
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
			if (m_ftpClient.isConnected()) {
				this.disconnect();
				this.setConnectFlag(false);
			}
			System.out.println("FTP Server Connect Failure!");
		}
	}

	/**
	 * Logs out and disconnects from the server
	 */
	public void disconnect() {

		try {
			m_ftpClient.logout();
			m_ftpClient.disconnect();

			this.setConnectFlag(false);
			System.out.println("FTP Server Disconnect!");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Determines whether a directory exists or not
	 * 
	 * @param dirPath
	 * @return true if exists, false otherwise
	 * @throws IOException
	 *             thrown if any I/O error occurred.
	 */
	public boolean checkDirectoryExists(String dirPath) throws IOException {
		Boolean exists = m_ftpClient.changeWorkingDirectory(dirPath);
		if (!exists) {
			return false;
		}
		return true;
	}

	/**
	 * Determines whether a file exists or not
	 * 
	 * @param filePath
	 * @return true if exists, false otherwise
	 * @throws IOException
	 *             thrown if any I/O error occurred.
	 */
	public boolean checkFileExists(String filePath) throws IOException {
		InputStream inputStream = m_ftpClient.retrieveFileStream(filePath);
		m_returnCode = m_ftpClient.getReplyCode();
		if (inputStream == null || m_returnCode == 550) {
			return false;
		}
		return true;
	}

	/**
	 * Creates a nested directory structure on a FTP server
	 * 
	 * @param dirPath
	 *            Path of the directory, i.e /projects/java/ftp/demo
	 * @return true if the directory was created successfully, false otherwise
	 * @throws IOException
	 *             if any error occurred during client-server communication
	 */
	public boolean makeDirectories(String dirPath) throws IOException {
		String[] pathElements = dirPath.split("/");

		if (pathElements != null && pathElements.length > 0) {
			for (String singleDir : pathElements) {
				boolean existed = m_ftpClient.changeWorkingDirectory(singleDir);
				if (!existed) {
					boolean created = m_ftpClient.makeDirectory(singleDir);
					if (created) {
						System.out.println("CREATED directory: " + singleDir);
						m_ftpClient.changeWorkingDirectory(singleDir);
					} else {
						System.out.println("fail to create directory.. [" + dirPath+ "]");
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Removes a non-empty directory by delete all its sub files and sub
	 * directories recursively. And finally remove the directory.
	 */
	public Boolean removeDirectory(String parentDir, String currentDir) throws IOException {
		String dirToList = parentDir;
		if (!currentDir.equals("")) {
			dirToList += "/" + currentDir;
		}

		String systemtype = m_connInfo.getSystem();

		if ("WINDOWS".equals(systemtype)) {
			m_ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_NT));
		} else if ("OS/2".equals(systemtype)) {
			m_ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_OS2));
		} else if ("OS/400".equals(systemtype)) {
			m_ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_OS400));
		} else if ("MVS".equals(systemtype)) {
			m_ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_MVS));
		} else if ("VMS".equals(systemtype)) {
			m_ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_VMS));
		} else {
			m_ftpClient.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
		}

		FTPFile[] subFiles = m_ftpClient.listFiles(dirToList);

		if (subFiles != null && subFiles.length > 0) {
			for (FTPFile aFile : subFiles) {
				String currentFileName = aFile.getName();
				if (currentFileName.equals(".") || currentFileName.equals("..")) {
					// skip parent directory and the directory itself
					continue;
				}
				String filePath = parentDir + "/" + currentDir + "/" + currentFileName;
				if (currentDir.equals("")) {
					filePath = parentDir + "/" + currentFileName;
				}

				if (aFile.isDirectory()) {
					// remove the sub directory
					removeDirectory(dirToList, currentFileName);
				} else {
					// delete the file
					boolean deleted = m_ftpClient.deleteFile(filePath);
					if (deleted) {
						System.out.println("DELETED the file: " + filePath);
					} else {
						System.out.println("CANNOT delete the file: " + filePath);
					}
				}
			}

			// finally, remove the directory itself
			boolean removed = m_ftpClient.removeDirectory(dirToList);
			if (removed) {
				System.out.println("REMOVED the directory: " + dirToList);
			} else {
				System.out.println("CANNOT remove the directory: " + dirToList);
				return false;
			}
		} else {
			System.out.println("directory is not exists : " + dirToList);
			return false;
		}

		return true;
	}

	/**
	 * File Upload
	 */
	public Boolean uploadFile(Object contents, File newFile) {
		boolean flag = false;
		InputStream input = null;
		File srcFile = null;

		if (contents instanceof File) {
			srcFile = (File) contents;
			if (!srcFile.renameTo(newFile)) {
				srcFile.delete();
			}
			try {
				input = new FileInputStream(newFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (contents instanceof byte[]) {
			input = new ByteArrayInputStream((byte[]) contents);
		} else if (contents == null) {
			System.out.println( (new StringBuffer("Requested File is null !")).toString());
		} else {
			System.out.println( (new StringBuffer("Requested File is wrong ! [" + contents + "]")).toString());
		}

		try {

			// targetName 으로 파일이 올라간다
			if (m_ftpClient.storeFile(newFile.getName(), input)) {
				flag = true;
			}
		} catch (IOException e) {
			System.out.println("File Upload Failure!");
			return flag;
		}

		return flag;

	}

	public boolean downloadFile(String source, String target, String name) {

		boolean flag = false;

		OutputStream output = null;
		try {
			// 받는 파일 생성 이 위치에 이 이름으로 파일 생성된다
			File local = new File( name );
			output = new FileOutputStream( local );
			System.out.println("** There is downloadable directories.");
		} catch (FileNotFoundException fnfe) {
			System.out.println("There is not downloadable directories.");
			return flag;
		}

		File file = new File(source);
		try {
			if (m_ftpClient.retrieveFile(source, output)) {
				flag = true;
			}
			System.out.println("File Download Success..");
		} catch (IOException ioe) {
			System.out.println("File Download Failure!!");
		}
		return flag;
	}

	// 파일을 전송 받는다 위의 method 와 return 값이 달라서 하나 더 만들었다
	public File downloadFile(String source, String name) {

		OutputStream output = null;
		File local = null;
		try {
			// 받는 파일 생성
			local = new File( name );
			output = new FileOutputStream(local);
		} catch (FileNotFoundException fnfe) {
			System.out.println("There is not downloadable directories.");
		}
		
		File file = new File(source);
		try {
			if (m_ftpClient.retrieveFile(source, output)) {
				//
			}
		} catch (IOException ioe) {
			System.out.println("File Download Failure!!");
		}
		return local;
	}

	public Boolean removeFile(String filePath) throws IOException {
		int lastdirpoint = filePath.lastIndexOf("/");
		String delFileDir = filePath.substring(0, lastdirpoint);
		String delFilename = filePath.substring(lastdirpoint + 1);
		
		int lastpoint = delFilename.lastIndexOf(".");
		String strPath = delFilename.substring(0, lastpoint);
		String strExt = delFilename.substring(lastpoint + 1);
		
		System.out.println("filePath : " + filePath);
		System.out.println("lastdirpoint : " + lastdirpoint);
		System.out.println("delFileDir : " + delFileDir);
		System.out.println("delFilename : " + delFilename);
		System.out.println("strPath : " + strPath);
		System.out.println("strExt : " + strExt);
		
		m_ftpClient.changeWorkingDirectory(delFileDir);
		
		m_ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//		m_ftpClient.enterLocalPassiveMode();
		m_ftpClient.enterRemotePassiveMode();
		
//		if (!checkFileExists(filePath)) {
//			System.out.println("This file is not exist!");
//			return false;
//		}
		
		Boolean deleted = false;
		
		try {
//			m_ftpClient.rename(delFilename, strPath + "_del." + strExt);
//			m_ftpClient.sendCommand(FTPCmd.DELE, delFilename);
			
			
			deleted = m_ftpClient.deleteFile(delFilename);
			
			System.out.println("deleted Flag : " + deleted);
            if (deleted) {
            	System.out.println("The file was deleted successfully.");
            } else {
            	System.out.println("Could not delete the  file, it may not exist.");
            }

		} catch (FileNotFoundException fnfe) {
			System.out.println("There is not downloadable directories.");
		}
		
		return deleted;
	}
	
	public String getCurrentDirectory() throws IOException {
		return m_ftpClient.printWorkingDirectory();
	}
}
