package org.cometd.bayeux.client;


import org.cometd.bayeux.Session;



/**
 * @version $Revision$ $Date: 2009-12-08 09:42:45 +1100 (Tue, 08 Dec 2009) $
 */
public interface ClientSession extends Session
{
    void batch(Runnable batch);

    void disconnect();
    void endBatch();
    SessionChannel getSessionChannel(String channelName);

    void startBatch();

}
