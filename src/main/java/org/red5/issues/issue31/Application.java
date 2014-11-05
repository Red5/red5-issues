/*
 * RED5 Open Source Flash Server - http://code.google.com/p/red5/
 * 
 * Copyright 2006-2014 by respective authors (see below). All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.red5.issues.issue31;

import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamAwareScopeHandler;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;

/**
 * Main application.
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class Application extends MultiThreadedApplicationAdapter {

	private static Logger log = Red5LoggerFactory.getLogger(Application.class, "issues"); 
		
@Override
public boolean appConnect(IConnection conn, Object[] params){
    if(params.length>0 && params[0] instanceof String){
                Red5.getConnectionLocal().setAttribute("Connection", params[0]);
    }
    return super.appConnect(conn,params);
}

@Override
public void disconnect(IConnection conn,  IScope scope){
  log.info("Connection attribute: {}", Red5.getConnectionLocal().getAttribute("Connection"));   
  log.info("Connection - {}:{}", Red5.getConnectionLocal().getRemoteAddress(), Red5.getConnectionLocal().getRemotePort());
}

}
