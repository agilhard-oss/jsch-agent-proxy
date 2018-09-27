/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
package net.agilhard.jsch.agentproxy.examples;

import net.agilhard.jsch.agentproxy.AgentProxy;
import net.agilhard.jsch.agentproxy.AgentProxyException;
import net.agilhard.jsch.agentproxy.Identity;
import net.agilhard.jsch.agentproxy.connector.PageantConnector;

public class UsingPageant {
  public static void main(String[] arg){

    try{
      AgentProxy ap = new AgentProxy(new PageantConnector());

      Identity[] identities = ap.getIdentities();

      System.out.println("count: "+identities.length);

      for(int i=0; i<identities.length; i++){
        System.out.println("  comment: "+
                           new String(identities[i].getComment()));

        byte[] blob = identities[i].getBlob();
        System.out.print("  blob: ");
        for(int j=0; j<blob.length; j++){
          System.out.print(Integer.toHexString(blob[j]&0xff)+":");
        }
        System.out.println("");

        String data = "foo";
        byte[] signed = ap.sign(blob, data.getBytes());
        System.out.print("  sign: "+data+" -> ");
        for(int j=0; j<signed.length; j++){
          System.out.print(Integer.toHexString(signed[j]&0xff)+":");
        }
        System.out.println("");
      }
    }
    catch(AgentProxyException e){
      System.out.println(e);
    }
  }
}
