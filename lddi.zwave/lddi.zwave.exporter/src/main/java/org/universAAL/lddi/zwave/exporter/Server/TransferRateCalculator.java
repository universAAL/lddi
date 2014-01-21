/*
    Copyright 2007-2014 TSB, http://www.tsbtecnologias.es
    Technologies for Health and Well-being - Valencia, Spain

    See the NOTICE file distributed with this work for additional
    information regarding copyright ownership

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package org.universAAL.lddi.zwave.exporter.Server;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class to calculate transfer rates.
 * 
 * @author fraperod
 * @version $Rev: 5339 $ $Date: 2012-09-25 17:17:28 +0200 (mar, 25 sep 2012) $
 */
public class TransferRateCalculator extends TimerTask {


    /**
     * TransferRateCalculator calculationPeriod milliseconds.
     */
    protected long             calculationPeriod;

    /**
     * TransferRateCalculator calculationPeriondSeconds seconds.
     */
    private final float        calculationPeriondSeconds;

    /**
     * TransferRateCalculator defaultCalculationPeriod.
     */
    private static final long  DEFAULT_CALCULATION_PERIOD = 5000;

    /**
     * TransferRateCalculator periodIncomingBytes.
     */
    private long               previousPeriodIncomingBytes;
    /**
     * TransferRateCalculator periodOutgoingBytes.
     */
    private long               previousPeriodOutgoingBytes;

    /**
     * TransferRateCalculator totalIncomingBytes.
     * 
     * @uml.property name="totalIncomingBytes"
     */
    private long               totalIncomingBytes;
    /**
     * TransferRateCalculator totalOutgoingBytes.
     * 
     * @uml.property name="totalOutgoingBytes"
     */
    private long               totalOutgoingBytes;


    /**
     * TransferRateCalculator incomingByteRate.
     * 
     * @uml.property name="incomingByteRate"
     */
    private float              incomingByteRate;
    /**
     * TransferRateCalculator outgoingByteRate.
     * 
     * @uml.property name="outgoingByteRate"
     */
    private float              outgoingByteRate;


    /** Timer used to TransferRateCalculator calculations. */
    private static final Timer TIMER                      = new Timer();


    /**
     * Constructor.
     */
    public TransferRateCalculator() {
        this(DEFAULT_CALCULATION_PERIOD);
    }


    /**
     * Constructor.
     * 
     * @param calcPeriodMilisecons time to update calculations in milliseconds
     */
    public TransferRateCalculator(final long calcPeriodMilisecons) {
        this.calculationPeriod = calcPeriodMilisecons;
        this.calculationPeriondSeconds = (float) calcPeriodMilisecons / (float) 1000;

        TIMER.schedule(this, calculationPeriod, calculationPeriod);
    }


    /**
     * Returns the incoming byte rate.
     * 
     * @return the incoming byte rate
     * @uml.property name="incomingByteRate"
     */
    public float getIncomingByteRate() {
        return incomingByteRate;
    }


    /**
     * Returns the outgoing byte rate.
     * 
     * @return the outgoing byte rate
     * @uml.property name="outgoingByteRate"
     */
    public float getOutgoingByteRate() {
        return outgoingByteRate;
    }


    /**
     * Returns the total incoming bytes.
     * 
     * @return the total incoming bytes
     * @uml.property name="totalIncomingBytes"
     */
    public long getTotalIncomingBytes() {
        return totalIncomingBytes;
    }


    /**
     * Returns the total outgoing bytes.
     * 
     * @return the total outgoing bytes
     * @uml.property name="totalOutgoingBytes"
     */
    public long getTotalOutgoingBytes() {
        return totalOutgoingBytes;
    }


    /**
     * New data received.
     * 
     * @param dataSize size of the new incoming data
     */
    public void newIncomingData(final long dataSize) {
        // synchronized (lock) {
        totalIncomingBytes += dataSize;

        // }
    }


    /**
     * New data sent.
     * 
     * @param dataSize size of the new outgoing data
     */
    public void newOutgoingData(final long dataSize) {
        // synchronized (lock) {
        totalOutgoingBytes += dataSize;

        // }

    }


    /**
     * Run.
     */
    @Override
    public void run() {
        synchronized (this) {
            incomingByteRate = (totalIncomingBytes - previousPeriodIncomingBytes) / calculationPeriondSeconds;
            outgoingByteRate = (totalOutgoingBytes - previousPeriodOutgoingBytes) / calculationPeriondSeconds;

            previousPeriodIncomingBytes = totalIncomingBytes;
            previousPeriodOutgoingBytes = totalOutgoingBytes;
        }
    }
}
