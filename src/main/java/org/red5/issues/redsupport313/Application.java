package org.red5.issues.redsupport313;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IAttributeStore;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.so.ISharedObjectBase;
import org.red5.server.api.so.ISharedObjectListener;

/**
 * Internal IR5 issue https://infrared5.atlassian.net/browse/REDSUPPORT-313
 */
public class Application extends MultiThreadedApplicationAdapter {

    private final String soName = "soName";

    @Override
    public boolean roomStart(IScope currentScope) {
        log.info("roomStart: {}", currentScope.toString());
        return true;
    }

    @Override
    public boolean roomConnect(IConnection conn, Object[] params) {
        // get the client
        return true;
    }

    @Override
    public boolean roomJoin(IClient client, IScope room) {
        log.info("roomJoin: {} {}", client, room.toString());
        IScope currentScope = Red5.getConnectionLocal().getScope();
        ISharedObject so = getSharedObject(currentScope, soName, false);
        ExampleObject obj = null;
        if (so.hasAttribute("object1")) {
            // if the object exists in the so already, update it with the new client
            obj = (ExampleObject) so.getAttribute("object1");
            obj.clientId.add(client.getId());
        } else {
            // create a new object
            obj = new ExampleObject();
            obj.siteId = "Example String";
            obj.clientId.add(client.getId());
            // set a listener the first time we create the obj
            so.addSharedObjectListener(new SOListener());
            so.setAttribute("object1", obj);
        }
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
        try {
            IConnection conn = Red5.getConnectionLocal();
            ISharedObject so = getSharedObject(conn.getScope(), soName, false);
            //so.beginUpdate(conn);
            ExampleObject obj = (ExampleObject) so.getAttribute("object1");
            log.info("updateSo old VALUES(" + obj.random1 + " " + obj.random2 + ")");
            obj.random1 = randomNr1;
            obj.random2 = randomNr2;
            log.info("updateSo new VALUES(" + obj.random1 + " " + obj.random2 + ")");
            so.setAttribute("object1", obj);
            // set dirty flag if modifying the attribute object directly
            so.setDirty(true);
            //so.endUpdate();
        } catch (Exception e) {
            log.warn("Ex on updateSo", e);
        }
    }

    public void getMap(Map<String, Object> map) {
        log.info("getMap: {}", map);
        IConnection conn = Red5.getConnectionLocal();
        ISharedObject so = getSharedObject(conn.getScope(), soName);
        if (map == null) {
            map = new HashMap<>();
        }
        map.put("key1", RandomStringUtils.randomAscii(6));
        map.put("key2", RandomStringUtils.randomAscii(6));
        // set a listener the first time we create the obj
        so.addSharedObjectListener(new SOListener());
        so.setAttribute("attributeMap", map);
        so.setDirty(true);
    }
    
    private final class SOListener implements ISharedObjectListener {

        public void onSharedObjectClear(ISharedObjectBase so) {
            log.debug("onSharedObjectClear: {}", so);
        }

        public void onSharedObjectConnect(ISharedObjectBase so) {
            log.debug("onSharedObjectConnect: {}", so);
        }

        public void onSharedObjectDelete(ISharedObjectBase so, String key) {
            log.debug("onSharedObjectDelete key: {}", key);
        }

        public void onSharedObjectDisconnect(ISharedObjectBase so) {
            log.debug("onSharedObjectDisconnect: {}", so);
        }

        public void onSharedObjectSend(ISharedObjectBase so, String method, List<?> attributes) {
            log.debug("onSharedObjectSend - method: {} {}", method, attributes);
        }

        public void onSharedObjectUpdate(ISharedObjectBase so, IAttributeStore attributes) {
            log.debug("onSharedObjectUpdate - {}", attributes);
        }

        public void onSharedObjectUpdate(ISharedObjectBase so, Map<String, Object> attributes) {
            log.debug("onSharedObjectUpdate - {}", attributes);
        }

        public void onSharedObjectUpdate(ISharedObjectBase so, String key, Object value) {
            log.debug("onSharedObjectUpdate - {} = {}", key, value);
        }

    }

}
