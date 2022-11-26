package com.fr3ts0n.ecu;

import java.util.Comparator;

/**
 * OBD PID definition
 * - Allow prioritization of PID requests by providing:
 *   - timestamp (ms) of next expected request
 *   - sort algorithm by request timestamp for PID Collections
 */
public class ObdPid
    extends Number
{
    /** The PID value itself */
    private final int pid;
    /** Timestamp (system ms) for next expected data request */
    private long nextRequest_ms = 0;

    public ObdPid(int pidCode)
    {
        pid = pidCode;
    }

    @Override
    public double doubleValue() {
        return (double)pid;
    }

    @Override
    public float floatValue() {
        return (float)pid;
    }

    @Override
    public int intValue() {
        return pid;
    }

    @Override
    public long longValue() {
        return (long)pid;
    }

    @Override
    public String toString() {
        return Integer.toString(pid, 16);
    }

    /**
     * Set timestamp of next expected PID request
     * @param _nextRequest Timestamp [ms] of next expected request
     */
    public void setNextRequest(long _nextRequest)
    {
        nextRequest_ms = _nextRequest;
    }

    /**
     * Get timestamp of next expected PID request
     * @return Timestamp of next expected PID request
     */
    public long getNextRequest()
    {
        return nextRequest_ms;
    }

    /**
     * Comparator to allow list / vector sorting by next request
     */
    public static Comparator<ObdPid> requestSorter = new Comparator<ObdPid>()
    {
        @Override
        public int compare(ObdPid arg0, ObdPid arg1)
        {
            return Long.compare(arg0.nextRequest_ms, arg1.nextRequest_ms);
        }
    };
}
