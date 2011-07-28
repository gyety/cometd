/*
 * Copyright (c) 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cometd.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cometd.bayeux.server.ServerTransport;
import org.cometd.common.AbstractTransport;
import org.eclipse.jetty.util.ajax.JSON;


/* ------------------------------------------------------------ */
/** The base class of all server transports.
 * <p>
 * Each derived Transport class should declare all options that it supports
 * by calling {@link #setOption(String, Object)} for each option.
 * Then during the call the {@link #init()}, each transport should
 * call the variants of {@link #getOption(String)} to obtained the configured
 * value for the option.
 *
 */
public abstract class AbstractServerTransport extends AbstractTransport implements ServerTransport
{
    public final static String TIMEOUT_OPTION="timeout";
    public final static String INTERVAL_OPTION="interval";
    public final static String MAX_INTERVAL_OPTION="maxInterval";
    public final static String MAX_LAZY_OPTION="maxLazyTimeout";
    public final static String META_CONNECT_DELIVERY_OPTION="metaConnectDeliverOnly";

    private final BayeuxServerImpl _bayeux;
    private long _interval=0;
    private long _maxInterval=10000;
    private long _timeout=30000;
    private long _maxLazyTimeout=5000;
    private boolean _metaConnectDeliveryOnly=false;
    private Object _advice;

    private String _optionPrefix="";



    /* ------------------------------------------------------------ */
    /** Construct a ServerTransport.
     * </p>
     * <p>The construct is passed the {@link BayeuxServerImpl} instance for
     * the transport.  The {@link BayeuxServerImpl#getOptions()} map is
     * populated with the default options known by this transport. The options
     * are then inspected again when {@link #init()} is called, to set the
     * actual values used.  The options are arranged into a naming hierarchy
     * by derived classes adding prefix segments by calling add {@link #addPrefix(String)}.
     * Calls to {@link #getOption(String)} will use the list of prefixes
     * to search for the most specific option set.
     * </p>
     * <p>
     */
    protected AbstractServerTransport(BayeuxServerImpl bayeux, String name)
    {
        super(name,bayeux.getOptions());
        _bayeux=bayeux;
    }


    /* ------------------------------------------------------------ */
    public Object getAdvice()
    {
        return _advice;
    }

    /* ------------------------------------------------------------ */
    /** Get the interval.
     * @return the interval
     */
    public long getInterval()
    {
        return _interval;
    }

    /* ------------------------------------------------------------ */
    /** Get the maxInterval.
     * @return the maxInterval
     */
    public long getMaxInterval()
    {
        return _maxInterval;
    }


    /* ------------------------------------------------------------ */
    /** Get the max time before dispatching lazy message.
     * @return the max lazy timeout in MS
     */
    public long getMaxLazyTimeout()
    {
        return _maxLazyTimeout;
    }

    /* ------------------------------------------------------------ */
    /** Get the timeout.
     * @return the timeout
     */
    public long getTimeout()
    {
        return _timeout;
    }

    /* ------------------------------------------------------------ */
    public boolean isMetaConnectDeliveryOnly()
    {
        return _metaConnectDeliveryOnly;
    }

    /* ------------------------------------------------------------ */
    public void setMetaConnectDeliveryOnly(boolean meta)
    {
        _metaConnectDeliveryOnly=meta;
    }

    /* ------------------------------------------------------------ */
    /** Initialise the transport.
     * Initialise the transport, resolving default and direct options.
     * After the call to init, the {@link #getMutableOptions()} set should
     * be reset to reflect only the options that can be changed on a running
     * transport.
     * This implementation clears the mutable options set.
     */
    protected void init()
    {
        _interval=getOption(INTERVAL_OPTION,_interval);
        _maxInterval=getOption(MAX_INTERVAL_OPTION,_maxInterval);
        _timeout=getOption(TIMEOUT_OPTION,_timeout);
        _maxLazyTimeout=getOption(MAX_LAZY_OPTION,_maxLazyTimeout);
        _metaConnectDeliveryOnly=getOption(META_CONNECT_DELIVERY_OPTION,_metaConnectDeliveryOnly);

        _advice=new JSON.Literal("{\"reconnect\":\"retry\",\"interval\":" + _interval + ",\"timeout\":" + _timeout + "}");
    }



    /* ------------------------------------------------------------ */
    /** Get the bayeux.
     * @return the bayeux
     */
    public BayeuxServerImpl getBayeux()
    {
        return _bayeux;
    }

    /* ------------------------------------------------------------ */
    /** Set the interval.
     * @param interval the interval to set
     */
    public void setInterval(long interval)
    {
        _interval = interval;
    }

    /* ------------------------------------------------------------ */
    /** Set the maxInterval.
     * @param maxInterval the maxInterval to set
     */
    public void setMaxInterval(long maxInterval)
    {
        _maxInterval = maxInterval;
    }

    /* ------------------------------------------------------------ */
    /** Set the timeout.
     * @param timeout the timeout to set
     */
    public void setTimeout(long timeout)
    {
        _timeout = timeout;
    }

    /* ------------------------------------------------------------ */
    /** Set the maxLazyTimeout.
     * @param maxLazyTimeout the maxLazyTimeout to set
     */
    public void setMaxLazyTimeout(long maxLazyTimeout)
    {
        _maxLazyTimeout = maxLazyTimeout;
    }

    /* ------------------------------------------------------------ */
    /** Set the advice.
     * @param advice the advice to set
     */
    public void setAdvice(Object advice)
    {
        _advice = advice;
    }

    /* ------------------------------------------------------------ */
    /**
     * Housekeeping sweep, called a regular intervals
     */
    protected void sweep()
    {
    }

    /* ------------------------------------------------------------ */
    public interface Scheduler
    {
        void cancel();
        void schedule();
    }

    /* ------------------------------------------------------------ */
    public interface OneTimeScheduler extends Scheduler
    {}
}
