/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.vm.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.AbstractServiceAndFlowTestCase;

public class PersistentUnaddressedVmQueueTestCase extends AbstractServiceAndFlowTestCase
{

    private static final int RECEIVE_TIMEOUT = 5000;

    public PersistentUnaddressedVmQueueTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "vm/persistent-unaddressed-vm-queue-test-service.xml"},
            {ConfigVariant.FLOW, "vm/persistent-unaddressed-vm-queue-test-flow.xml"}});
    }

    @Test
    public void testAsynchronousDispatching() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://receiver1?connector=Connector1", "Test", null);
        MuleMessage result = client.request("vm://out?connector=Connector2", RECEIVE_TIMEOUT);
        assertNotNull(result);
        assertEquals(result.getPayloadAsString(), "Test");
    }
}
