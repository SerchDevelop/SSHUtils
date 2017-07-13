package mx.indra.ingenset.utils.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SSHUtils {

	Properties prop = new Properties();
	InputStream input = null;

	private String tipoDespiegue;
	private String userTomcat;
	private String userOracle;
	private String passwordTomcat;
	private String passwordOracle;
	private String hostTomcat;
	private String hostOracle;
	private Integer port;
	private String remoteFileTomcat;
	private String remoteFileOracle;
	private String typeFile;
	
	static Session sessionTomcat = null;
	static Session sessionOracle = null;
	static ChannelSftp sftpChannelTomcat = null;
	static ChannelSftp sftpChannelOracle = null;
	
	HttpServletRequest request;
	HttpServletResponse response;
	
	public SSHUtils(){
		
	}
	
	public SSHUtils(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
	}

	public void loadProperties(){
		System.out.println("Entering loadProperties");
		
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			input = this.getClass().getClassLoader().getResourceAsStream("resources/config.properties");
//			input = new FileInputStream(".resources/config.properties");
			System.out.println("Path :: " + input);
			prop.load(input);
			this.tipoDespiegue = prop.getProperty("config.tipoDespiegue");
			this.port = Integer.parseInt(prop.getProperty("config.port").toString());
			this.remoteFileTomcat = prop.getProperty("config.remoteFileTomcat");
			this.remoteFileOracle = prop.getProperty("config.remoteFileOracle");
			this.typeFile = prop.getProperty("config.typeFile");
			this.userTomcat = prop.getProperty("config.userTomcat");
			this.userOracle = prop.getProperty("config.userOracle");
			
			if(this.tipoDespiegue.equals("1")){
				System.out.println("Despliegue para ambiente de desarrollo");
				this.passwordTomcat = prop.getProperty("config.passwordTomcatDev");
				this.passwordOracle = prop.getProperty("config.passwordOracleDev");
				this.hostTomcat = prop.getProperty("config.hostTomcatDev");
				this.hostOracle = prop.getProperty("config.hostOracleDev");				
			} else if(this.tipoDespiegue.equals("2")){
				System.out.println("Despliegue para ambiente de produccion");
				this.passwordTomcat = prop.getProperty("config.passwordTomcatProd");
				this.passwordOracle = prop.getProperty("config.passwordOracleProd");
				this.hostTomcat = prop.getProperty("config.hostTomcatProd");
				this.hostOracle = prop.getProperty("config.hostOracleProd");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void conectar(){
		if(this.tipoDespiegue.equals("1")){
			cargaLlavesDev();
		} else if(this.tipoDespiegue.equals("2")){
			cargaLlavesProd();
		}
		
    }
	
	
	public void cargaLlavesDev(){
		System.out.println("Comenzando a cargar llaves para desarrollo");
	    try {

	    	JSch jsch = new JSch();
	        String absoluteFilePathPrivatekey = "/";
	        File tmpFileObject = new File("resources/privateKeyTomcatDev");
	        System.out.println("tmpFileObject :: " + tmpFileObject.getAbsolutePath());
	        if (tmpFileObject.exists() && tmpFileObject.isFile()){
	          absoluteFilePathPrivatekey = tmpFileObject.getAbsolutePath();
	        }

	        jsch.addIdentity(absoluteFilePathPrivatekey, passwordTomcat);

	        // Tomcat
	    	sessionTomcat = jsch.getSession(userTomcat, hostTomcat, port);
            sessionTomcat.setPassword(passwordTomcat);
            
            sessionTomcat.setConfig("StrictHostKeyChecking", "no");
	        System.out.println("\nEstablishing Connection...");
	        
	        sessionTomcat.connect();
            System.out.println("Connection established.");
	        System.out.println("Crating SFTP Channel.");
	        
	        sftpChannelTomcat = (ChannelSftp) sessionTomcat.openChannel("sftp");
	        sftpChannelTomcat.connect();
	        System.out.println("SFTP Channel created.\n");
	        
	        // Oracle
	        String absoluteFilePathPrivatekeyOracle = "/";
	        File tmpFileObjectOracle = new File("resources/privateKeyOracleDev");
	        if (tmpFileObjectOracle.exists() && tmpFileObjectOracle.isFile()){
	        	absoluteFilePathPrivatekeyOracle = tmpFileObjectOracle.getAbsolutePath();
	        }
	        jsch.addIdentity(absoluteFilePathPrivatekeyOracle, passwordOracle);

	    	sessionOracle = jsch.getSession(userOracle, hostOracle, port);
            sessionOracle.setPassword(passwordOracle);
            
            sessionOracle.setConfig("StrictHostKeyChecking", "no");
	        System.out.println("\nEstablishing Connection Oracle...");
	        
	        sessionOracle.connect();
            System.out.println("Connection established Oracle.");
	        System.out.println("Crating SFTP Channel Oracle.");
	        
	        sftpChannelOracle= (ChannelSftp) sessionOracle.openChannel("sftp");
	        sftpChannelOracle.connect();
	        System.out.println("SFTP Channel created Oracle.\n");
	        
        } catch(Exception error) {
        	sessionTomcat = null;
        	sftpChannelTomcat = null;
        	sftpChannelOracle = null;
        	sessionOracle = null;
        	System.err.print("Error al establecer la conexion :: \n" + error);
        }
    }

	
	public void cargaLlavesProd(){
		System.out.println("Comenzando a cargar llaves para produccion");
	    try {
	    	ClassLoader classLoader = getClass().getClassLoader();
	    	JSch jsch = new JSch();
	        String absoluteFilePathPrivatekey = "/";
	        File tmpFileObject = new File(classLoader.getResource("resources/privateKeyProdTomcat").getFile());
	        System.out.println("tmpFileObject :: " + tmpFileObject.getAbsolutePath());
	        if (tmpFileObject.exists() && tmpFileObject.isFile()){
	          absoluteFilePathPrivatekey = tmpFileObject.getAbsolutePath();
	        }
	        jsch.addIdentity(absoluteFilePathPrivatekey, passwordTomcat);
	        
	        // Tomcat
	    	sessionTomcat = jsch.getSession(userTomcat, hostTomcat, port);
            sessionTomcat.setPassword(passwordTomcat);
            
            sessionTomcat.setConfig("StrictHostKeyChecking", "no");
	        System.out.println("\nEstablishing Connection...");
	        
	        sessionTomcat.connect();
            System.out.println("Connection established.");
	        System.out.println("Crating SFTP Channel.");
	        
	        sftpChannelTomcat = (ChannelSftp) sessionTomcat.openChannel("sftp");
	        sftpChannelTomcat.connect();
	        System.out.println("SFTP Channel created.\n");
	        
	        // Oracle
	        String absoluteFilePathPrivatekeyOracle = "/";
	        File tmpFileObjectOracle = new File(".resources/privatekeyProdOracle");
	        if (tmpFileObjectOracle.exists() && tmpFileObjectOracle.isFile()){
	        	absoluteFilePathPrivatekeyOracle = tmpFileObjectOracle.getAbsolutePath();
	        }
	        
	        jsch.addIdentity(absoluteFilePathPrivatekeyOracle, passwordOracle);

	    	sessionOracle = jsch.getSession(userOracle, hostOracle, port);
            sessionOracle.setPassword(passwordOracle);
            
            sessionOracle.setConfig("StrictHostKeyChecking", "no");
	        System.out.println("\nEstablishing Connection Oracle...");
	        
	        sessionOracle.connect();
            System.out.println("Connection established Oracle.");
	        System.out.println("Crating SFTP Channel Oracle.");
	        
	        sftpChannelOracle= (ChannelSftp) sessionOracle.openChannel("sftp");
	        sftpChannelOracle.connect();
	        System.out.println("SFTP Channel created Oracle.\n");
	        
        } catch(Exception error) {
        	sessionTomcat = null;
        	sftpChannelTomcat = null;
        	sftpChannelOracle = null;
        	sessionOracle = null;
        	System.err.print("Error al establecer la conexion :: \n" + error);
        }
    }

	
	public void desconectar(){
		System.out.println("Desconectando..");
		sftpChannelOracle.disconnect();
		sftpChannelTomcat.disconnect();
	}
	
	public void copyFiles(){
		System.out.println("sftpChannelTomcat :: " + sftpChannelTomcat);
		try {
			Vector<ChannelSftp.LsEntry> list = sftpChannelTomcat.ls(this.remoteFileTomcat);
			Integer fileCopy = 0;
	        for (ChannelSftp.LsEntry oListItem : list) {
	    		if(oListItem.getFilename().contains(this.typeFile) || oListItem.getFilename().contains("xml")){
					File file = new File(oListItem.getFilename());
					sftpChannelOracle.put(sftpChannelTomcat.get(this.remoteFileTomcat + oListItem.getFilename()), this.remoteFileOracle + file.getName(), ChannelSftp.OVERWRITE);
					System.out.println("Se ha copiado el archivo: " + oListItem.getFilename());
					sftpChannelTomcat.rm(remoteFileTomcat + oListItem.getFilename());
					System.out.println("Se elimino archivo :: " + remoteFileTomcat + oListItem.getFilename());
					fileCopy++;
	    		}
	        }
	        System.out.println("Se copiaron: " + fileCopy + " archivos.");
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}

	public String getUserTomcat() {
		return userTomcat;
	}

	public void setUserTomcat(String userTomcat) {
		this.userTomcat = userTomcat;
	}

	public String getUserOracle() {
		return userOracle;
	}

	public void setUserOracle(String userOracle) {
		this.userOracle = userOracle;
	}

	public String getHostTomcat() {
		return hostTomcat;
	}

	public void setHostTomcat(String hostTomcat) {
		this.hostTomcat = hostTomcat;
	}

	public String getHostOracle() {
		return hostOracle;
	}

	public void setHostOracle(String hostOracle) {
		this.hostOracle = hostOracle;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getRemoteFileTomcat() {
		return remoteFileTomcat;
	}

	public void setRemoteFileTomcat(String remoteFileTomcat) {
		this.remoteFileTomcat = remoteFileTomcat;
	}

	public String getRemoteFileOracle() {
		return remoteFileOracle;
	}

	public void setRemoteFileOracle(String remoteFileOracle) {
		this.remoteFileOracle = remoteFileOracle;
	}

	public String getTypeFile() {
		return typeFile;
	}

	public void setTypeFile(String typeFile) {
		this.typeFile = typeFile;
	}

	public String getPasswordTomcat() {
		return passwordTomcat;
	}

	public void setPasswordTomcat(String passwordTomcat) {
		this.passwordTomcat = passwordTomcat;
	}

	public String getPasswordOracle() {
		return passwordOracle;
	}

	public void setPasswordOracle(String passwordOracle) {
		this.passwordOracle = passwordOracle;
	}
	public String getTipoDespiegue() {
		return tipoDespiegue;
	}
	public void setTipoDespiegue(String tipoDespiegue) {
		this.tipoDespiegue = tipoDespiegue;
	}
	    
}
