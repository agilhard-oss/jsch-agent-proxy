/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2011 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright 
     notice, this list of conditions and the following disclaimer in 
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package net.agilhard.jsch.agentproxy.connector.sshagent;

import net.agilhard.jsch.agentproxy.core.Connector;
import net.agilhard.jsch.agentproxy.core.Buffer;
import net.agilhard.jsch.agentproxy.core.AgentProxyException;
import net.agilhard.jsch.agentproxy.core.USocketFactory;

import java.io.IOException;

public class SSHAgentConnector implements Connector {
  private USocketFactory factory;
  private String usocketPath;

  public SSHAgentConnector(USocketFactory factory) throws AgentProxyException {
    this(factory, null);
  }
 
  public SSHAgentConnector(USocketFactory factory, String usocketPath) throws AgentProxyException {
    this.factory = factory;
    this.usocketPath = usocketPath;

    // checking if factory is really functional.
    USocketFactory.Socket sock = null;
    try {
      sock = open();
    }
    catch(IOException e){ throw new AgentProxyException(e.toString()); }
    catch(Exception e){ throw new AgentProxyException(e.toString()); }
    finally{
      try{
        if(sock!=null)
          sock.close();
      }
      catch(IOException e){
        throw new AgentProxyException(e.toString());
      }
    }
  }

  public String getName(){
    return "ssh-agent";
  }

  public static boolean isConnectorAvailable(){
    return isConnectorAvailable(null);
  }

  public static boolean isConnectorAvailable(String usocketPath){
    return System.getenv("SSH_AUTH_SOCK")!=null || usocketPath!=null;
  }

  public boolean isAvailable(){
    return isConnectorAvailable();
  }

  private USocketFactory.Socket open() throws IOException {
    String ssh_auth_sock = usocketPath;
    if(ssh_auth_sock == null) {
      ssh_auth_sock = System.getenv("SSH_AUTH_SOCK");
    }
    if(ssh_auth_sock == null) {
      throw new IOException("SSH_AUTH_SOCK is not defined.");
    } 
    return factory.open(ssh_auth_sock);
  }

  public void query(Buffer buffer) throws AgentProxyException {
    USocketFactory.Socket sock = null;
    try {
      sock = open();
      sock.write(buffer.buffer, 0, buffer.getLength());
      buffer.rewind();
      int i = sock.readFull(buffer.buffer, 0, 4);  // length
      i = buffer.getInt();
      buffer.rewind();
      buffer.checkFreeSize(i);
      i = sock.readFull(buffer.buffer, 0, i);
    }
    catch(IOException e){
      throw new AgentProxyException(e.toString());
    }
    finally {
      try {
        if(sock!=null)
          sock.close();
      }
      catch(IOException e){
        throw new AgentProxyException(e.toString());
      }
    }
  }
}
