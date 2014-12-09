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

package org.red5.issues.issue39;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IBroadcastStream;
import org.slf4j.Logger;

/**
 * Main application for https://github.com/Red5/red5-server/issues/39
 * 
 * @author Paul Gregoire (mondain@gmail.com)
 */
public class Application extends MultiThreadedApplicationAdapter {

	private static Logger log = Red5LoggerFactory.getLogger(Application.class, "issues");

	private CopyOnWriteArrayList<String> streamsInRoom = new CopyOnWriteArrayList<String>();

	@Override
	public boolean connect(IConnection conn, IScope scope, Object[] params) {
		log.info("connect - client id: {} address: {}:{}", Red5.getConnectionLocal().getClient().getId(), Red5.getConnectionLocal().getRemoteAddress(), Red5.getConnectionLocal().getRemotePort());
		return super.connect(conn, scope, params);
	}

	@Override
	public void streamBroadcastStart(IBroadcastStream stream) {
		String currentClientId = Red5.getConnectionLocal().getClient().getId();
		log.info("streamBroadcastStart - client id: {}", currentClientId);

		((IServiceCapableConnection) Red5.getConnectionLocal()).invoke("clientConnectionSuccess", new Object[] { Red5.getConnectionLocal() });

		for (String streamName : streamsInRoom) {
			((IServiceCapableConnection) Red5.getConnectionLocal()).invoke("newStreamInRoom", new Object[] { streamName });
		}

		streamsInRoom.add(stream.getPublishedName());

		// skipping save

		IClient newClient = Red5.getConnectionLocal().getClient();
		Iterator<IConnection> itConnections = Red5.getConnectionLocal().getScope().getClientConnections().iterator();
		while (itConnections.hasNext()) {
			IConnection conn = itConnections.next();
			if (!conn.getClient().getId().equals(newClient.getId()) && conn instanceof IServiceCapableConnection) {
				((IServiceCapableConnection) conn).invoke("newStreamInRoom", new Object[] { stream.getPublishedName() });
			}
		}

		//super.streamBroadcastStart(stream);
	}

	@Override
	public void streamBroadcastClose(IBroadcastStream stream) {
		IConnection conn = Red5.getConnectionLocal();
		String currentClientId = conn.getClient().getId();
		log.info("streamBroadcastClose - client id: {}", currentClientId);
		
		streamsInRoom.remove(stream.getPublishedName());
		
		//super.streamBroadcastClose(stream);
	}

	@Override
	public void disconnect(IConnection conn, IScope scope) {
		log.info("disconnect - client id: {} address: {}:{}", Red5.getConnectionLocal().getClient().getId(), Red5.getConnectionLocal().getRemoteAddress(), Red5.getConnectionLocal().getRemotePort());
		super.disconnect(conn, scope);
	}

}
