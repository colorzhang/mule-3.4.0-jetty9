/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.http.functional;

import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transport.http.HttpConstants;
import org.mule.util.ClassUtils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StaticResourcesMPFunctionalTestCase extends FunctionalTestCase
{
    @ClassRule
    public static DynamicPort port1 = new DynamicPort("port1");

    @ClassRule
    public static DynamicPort port2 = new DynamicPort("port2");

    private HttpMethod method;
    private int responseCode;
    private String payload;

    public StaticResourcesMPFunctionalTestCase()
    {
        System.setProperty("test.root",
            ClassUtils.getClassPathRoot(StaticResourcesMPFunctionalTestCase.class).getPath());
        setDisposeContextPerClass(true);
    }

    @Override
    protected String getConfigResources()
    {
        return "http-static-resource-test.xml";
    }

    @Test
    public void httpUrlWithoutExplicitResourceShouldReturnDefaultDocument() throws Exception
    {
        String url = String.format("http://localhost:%d/static", port1.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_OK, responseCode);
        assertEquals(payload, "Test index.html");
    }

    @Test
    public void httpUrlRequestingExplicitResourceShouldReturnResource() throws Exception
    {
        String url = String.format("http://localhost:%d/static/main.html", port1.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_OK, responseCode);
        assertEquals(payload, "Test main.html");
    }

    @Test
    public void httpUrlRequestingNonexistentResourceShouldReturnNotFoundStatus() throws Exception
    {
        String url = String.format("http://localhost:%d/static/foo.html", port1.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_NOT_FOUND, responseCode);
    }

    @Test
    public void contentTypeForDefaultResourceShouldBeTextHtml() throws Exception
    {
        String url = String.format("http://localhost:%d/static", port1.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_OK, responseCode);
        assertResponseContentType("text/html");
    }

    @Test
    public void contentTypeShouldBeDetermintedFromResource() throws Exception
    {
        String url = String.format("http://localhost:%d/static/image.gif", port1.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_OK, responseCode);
        assertResponseContentType("image/gif");
    }

    @Test
    public void explicitMimeTypeConfigurationShouldOverrideDefaults() throws Exception
    {
        String url = String.format("http://localhost:%d/static/image.png", port1.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_OK, responseCode);
        assertResponseContentType("image/png");
    }

    @Test
    public void httpsUrlWithoutExplicitResourceShouldReturnDefaultDocument() throws Exception
    {
        String url = String.format("https://localhost:%d/static", port2.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_OK, responseCode);
        assertEquals(payload, "Test index.html");
    }

    @Test
    public void httpsUrlRequestingExplicitResourceShouldReturnResource() throws Exception
    {
        String url = String.format("https://localhost:%d/static/main.html", port2.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_OK, responseCode);
        assertEquals(payload, "Test main.html");
    }

    @Test
    public void httpsUrlRequestingNonexistentResourceShouldReturnNotFoundStatus() throws Exception
    {
        String url = String.format("https://localhost:%d/static/foo.html", port2.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_NOT_FOUND, responseCode);
    }

    private void request(String url) throws Exception
    {
        method = new GetMethod(url);
        responseCode = new HttpClient().executeMethod(method);
        payload = method.getResponseBodyAsString();
    }

    private void assertResponseContentType(String contentType)
    {
        assertEquals(contentType, method.getResponseHeader("Content-Type").getValue());
    }

    /**
     * Test that endpoints bound to the same http port but different path work with
     * the static resource MP
     */
    @Test
    public void testFlowBindingOnSamePort() throws Exception
    {
        String url = String.format("http://localhost:%d/echo", port1.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_OK, responseCode);
        assertEquals(method.getResponseBodyAsString(), "/echo");

        url = String.format("https://localhost:%d/echo", port2.getNumber());
        request(url);
        assertEquals(HttpConstants.SC_OK, responseCode);
        assertEquals(method.getResponseBodyAsString(), "/echo");
    }
}
