package com.poscoict.gluewing.filetransfer;

import java.util.Properties;

public class FTPConnInfo {
	private String m_ftpUrl   = null;
	private int m_ftpPort;
	private String m_userName = null;
	private String m_passwd   = null;
	private String m_defDir   = null;
	private String m_system   = null;

	public FTPConnInfo(Properties props) {
		setFTPInfo( props );
	}

	private void setFTPInfo(Properties props) {
		try {
			this.m_ftpUrl   = props.getProperty(FTPConstants.FTP_URL);
			this.m_ftpPort  = Integer.parseInt( props.getProperty(FTPConstants.FTP_PORT) );
			this.m_userName = props.getProperty(FTPConstants.FTP_USER_NAME);
			this.m_passwd   = props.getProperty(FTPConstants.FTP_PASSWORD);
			this.m_defDir   = props.getProperty(FTPConstants.FTP_DEF_DIR);
			this.m_system   = props.getProperty(FTPConstants.FTP_SYSTEM);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @return the m_ftpPort
	 */
	public int getFtpPort() {
		return m_ftpPort;
	}

	/**
	 * @param m_ftpPort
	 *            the m_ftpPort to set
	 */
	public void setFtpPort(int m_ftpPort) {
		this.m_ftpPort = m_ftpPort;
	}

	/**
	 * @return the m_ftpUrl
	 */
	public String getFtpUrl() {
		return m_ftpUrl;
	}

	/**
	 * @param m_ftpUrl
	 *            the m_ftpUrl to set
	 */
	public void setFtpUrl(String m_ftpUrl) {
		this.m_ftpUrl = m_ftpUrl;
	}

	/**
	 * @return the m_userName
	 */
	public String getUserName() {
		return m_userName;
	}

	/**
	 * @param m_userName
	 *            the m_userName to set
	 */
	public void setUserName(String m_userName) {
		this.m_userName = m_userName;
	}

	/**
	 * @return the m_passwd
	 */
	public String getPasswd() {
		return m_passwd;
	}

	/**
	 * @param m_passwd
	 *            the m_passwd to set
	 */
	public void setPasswd(String m_passwd) {
		this.m_passwd = m_passwd;
	}

	/**
	 * @return the m_defDir
	 */
	public String getDefDir() {
		return m_defDir;
	}

	/**
	 * @param m_defDir
	 *            the m_defDir to set
	 */
	public void setDefDir(String m_defDir) {
		this.m_defDir = m_defDir;
	}

	/**
	 * @return the m_system
	 */
	public String getSystem() {
		return m_system;
	}

	/**
	 * @param m_system the m_system to set
	 */
	public void setSystem(String m_system) {
		this.m_system = m_system;
	}

}
