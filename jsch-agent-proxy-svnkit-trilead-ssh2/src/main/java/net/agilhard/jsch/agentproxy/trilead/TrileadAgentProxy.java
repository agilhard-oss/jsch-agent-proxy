/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2013 ymnk, JCraft,Inc. All rights reserved.

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

package net.agilhard.jsch.agentproxy.trilead;

import net.agilhard.jsch.agentproxy.core.AgentProxy;
import net.agilhard.jsch.agentproxy.core.Connector;
import net.agilhard.jsch.agentproxy.core.Identity;

import java.util.ArrayList;
import java.util.Collection;

public class TrileadAgentProxy implements com.trilead.ssh2.auth.AgentProxy {
  private AgentProxy agent;

  public TrileadAgentProxy(Connector connector) {
    this.agent = new AgentProxy(connector);
  }

  public Collection getIdentities() {
    Identity[] identities = agent.getIdentities();
    ArrayList wrapped_identities = new ArrayList(identities.length);
    for( Identity identity : identities) {
      wrapped_identities.add(new TrileadAgentIdentity(this.agent, identity));
    }
    return wrapped_identities;
  }
}

