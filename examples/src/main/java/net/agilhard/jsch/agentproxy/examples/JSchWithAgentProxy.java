/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
package net.agilhard.jsch.agentproxy.examples;

import net.agilhard.jsch.*;
import net.agilhard.jsch.agentproxy.Connector;
import net.agilhard.jsch.agentproxy.AgentProxyException;
import net.agilhard.jsch.agentproxy.RemoteIdentityRepository;
import net.agilhard.jsch.agentproxy.ConnectorFactory;
import java.io.*;
import javax.swing.*; 

public class JSchWithAgentProxy {
  public static void main(String[] arg){

    try{
      JSch jsch=new JSch();

      jsch.setConfig("PreferredAuthentications", "publickey");

      Connector con = null;

      try {
        ConnectorFactory cf = ConnectorFactory.getDefault();
        con = cf.createConnector();
      }
      catch(AgentProxyException e){
        System.out.println(e);
      }

      if(con != null ){
        IdentityRepository irepo = new RemoteIdentityRepository(con);
        jsch.setIdentityRepository(irepo);
      }

      String host=null;
      if(arg.length>0){
        host=arg[0];
      }
      else{
        host=JOptionPane.showInputDialog("Enter username@hostname",
                                         System.getProperty("user.name")+
                                         "@localhost"); 
      }
      String user=host.substring(0, host.indexOf('@'));
      host=host.substring(host.indexOf('@')+1);

      Session session=jsch.getSession(user, host, 22);

      // username and passphrase will be given via UserInfo interface.
      UserInfo ui=new MyUserInfo();
      session.setUserInfo(ui);
      session.connect();

      Channel channel=session.openChannel("shell");

      ((ChannelShell)channel).setAgentForwarding(true);

      channel.setInputStream(System.in);
      channel.setOutputStream(System.out);

      channel.connect();

    }
    catch(Exception e){
      System.out.println(e);
    }
  }

  public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
    public String getPassword(){ return passwd; }
    public boolean promptYesNo(String str){ return true; }
    String passwd=null;
    public String getPassphrase(){ return null; }
    public boolean promptPassphrase(String message){ return true; }
    public boolean promptPassword(String message){ return true; }
    public void showMessage(String message){
    }
    public String[] promptKeyboardInteractive(String destination,
                                              String name,
                                              String instruction,
                                              String[] prompt,
                                              boolean[] echo){
        String[] response=new String[prompt.length];
        response[0] = passwd;
	return response;
    }
  }
}
