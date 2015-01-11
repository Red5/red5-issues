package org.red5.issues.issue46;

import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;

/**
 * Thanks to Remus Negrota for creating the application source for testing this issue.
 * 
 * @author Remus Negrota (remus@nusofthq.com)
 */
public class Application extends MultiThreadedApplicationAdapter {

	@Override
	public boolean roomStart(IScope currentScope) {
		log.info("roomStart room.toString()=" + currentScope.toString());
		return true;
	}

	@Override
	public boolean roomConnect(IConnection conn, Object[] params) {
		// get the client
		return true;
	}

	@Override
	public boolean roomJoin(IClient client, IScope room) {
		log.info("roomJoin(" + client.toString() + "," + room.toString() + ")");

		ExampleObject obj = new ExampleObject();
		obj.random1 = 0;
		obj.random2 = 0;
		obj.siteId = "Example String";
		obj.clientId.add("someID");

		/*Map<String, Object> obj = new HashMap<String, Object>();
		
		obj.put("random1", 0);
		obj.put("random2", 0);
		obj.put("siteId", "Example String");
		obj.put("clientId", "someID");*/

		IScope currentScope = Red5.getConnectionLocal().getScope();
		ISharedObject so = getSharedObject(currentScope, "example_so", false);
		so.setAttribute("object1", obj);

		return true;
	}

	@Override
	public void roomDisconnect(IConnection conn) {
		log.info("roomDisconnect(" + conn.toString() + ")");
	}

	@Override
	public void roomLeave(IClient client, IScope room) {
		log.debug("roomLeave(" + client.toString() + "," + room.toString() + ")");
	}

	@Override
	public void roomStop(IScope room) {
		log.debug("roomStop(" + room.toString() + ")");
	}

	@Override
	public boolean appStart(IScope app) {
		log = Red5LoggerFactory.getLogger(Application.class, app.getName());
		log.info("appStart(" + app.getContextPath() + " " + app.toString() + ") Application Started.");

		return true;
	}

	@Override
	public boolean appConnect(IConnection conn, Object[] params) {
		log.info("appConnect(" + conn.toString() + "," + params.toString() + ")");
		return true;
	}

	@Override
	public boolean appJoin(IClient client, IScope app) {
		log.info("appJoin(" + client.toString() + "," + app.toString() + ")");
		return true;
	}

	@Override
	public void appDisconnect(IConnection conn) {
		log.info("appDisconnect(" + conn.toString() + ")");

	}

	@Override
	public void appLeave(IClient client, IScope app) {
		log.info("appLeave(" + client.toString() + "," + app.toString() + ")");
		// we make sure the external users list is updated every time a user
		// leaves the chat
	}

	@Override
	public void appStop(IScope app) {
		log.info("appStop(" + app.toString() + ")");
	}

	public void updateSo(int randomNr1, int randomNr2) {
		log.info("updateSo(" + randomNr1 + " " + randomNr2 + ")");

		IScope currentScope = Red5.getConnectionLocal().getScope();
		ISharedObject so = getSharedObject(currentScope, "example_so", false);

		ExampleObject obj = (ExampleObject) so.getAttribute("object1");

		log.info("updateSo VALUES(" + obj.random1 + " " + obj.random2 + ")");

		obj.random1 = randomNr1;
		obj.random2 = randomNr2;

		/*Map <String, Object> obj = (HashMap<String, Object>) so.getAttribute("object1");
		obj.put("random1", randomNr1);
		obj.put("random2", randomNr2);*/

		so.setAttribute("object1", obj);
	}
	
}