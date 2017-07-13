package mx.indra.ingenset.test;

import mx.indra.ingenset.utils.ssh.SSHUtils;

public class LoadFileMain {

	public static void main(String[] args) {
        SSHUtils sshUtils = new SSHUtils();
        sshUtils.loadProperties();
		sshUtils.conectar();
//		sshUtils.copyFiles();
		sshUtils.desconectar();
	}

}
