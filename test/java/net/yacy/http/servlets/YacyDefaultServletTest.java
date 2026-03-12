// YacyDefaultServletTest.java
// Copyright 2016 by luccioman; https://github.com/luccioman
//
/*
This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but without any warranty; without even the implied warranty of merchantability or fitness for a particular purpose. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package net.yacy.http.servlets;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.net.HttpHeaders;

import net.yacy.cora.protocol.HeaderFramework;
import net.yacy.cora.protocol.RequestHeader;

/**
 * Unit tests for {@link YacyDefaultServlet} class.
 * @author luccioman
 *
 */
public class YacyDefaultServletTest {

	/**
	 * getContext() should be able fallback to default value with null or empty parameters.
	 */
	@Test
	public void testGetContextEmptyParams() {
		assertEquals("http://localhost:8090", YacyDefaultServlet.getContext(null, null));

		RequestHeader header = new RequestHeader();
		assertEquals("http://localhost:8090", YacyDefaultServlet.getContext(header, null));
	}
	
	/**
	 * getContext() : standard "Host" HTTP header is filled with host and port
	 */
	@Test
	public void testGetContextHostHeader() {
		RequestHeader header = new RequestHeader();
		header.put(HeaderFramework.HOST, "localhost:8090");
		assertEquals("http://localhost:8090", YacyDefaultServlet.getContext(header, null));
		
		header = new RequestHeader();
		header.put(HeaderFramework.HOST, "myhost.com:8090");
		assertEquals("http://myhost.com:8090", YacyDefaultServlet.getContext(header, null));
	}
	
	/**
	 * getContext() : standard "Host" header is filled with hostname and port, 
	 * custom "CONNECTION_PROP_PROTOCOL" header indicates the protocol
	 */
	@Test
    @SuppressWarnings("deprecation")
	public void testGetContextCustomProtocolHeader() {
		RequestHeader header = new RequestHeader();
		header.put(HeaderFramework.HOST, "myhost.com:8443");
		header.put(HeaderFramework.CONNECTION_PROP_PROTOCOL, "https");
		assertEquals("https://myhost.com:8443", YacyDefaultServlet.getContext(header, null));
		
		header = new RequestHeader();
		header.put(HeaderFramework.HOST, "myhost.com:8090");
		header.put(HeaderFramework.CONNECTION_PROP_PROTOCOL, "http");
		assertEquals("http://myhost.com:8090", YacyDefaultServlet.getContext(header, null));
	}
	
	/**
	 * getContext() : standard "Host" header is filled only with hostname (default standard port), 
	 * custom "CONNECTION_PROP_PROTOCOL" indicates the protocol
	 */
	@Test
    @SuppressWarnings("deprecation")
	public void testGetContextDefaultPortCustomProtocolHeader() {
		RequestHeader header = new RequestHeader();
		header.put(HeaderFramework.HOST, "myhost.com");
		header.put(HeaderFramework.CONNECTION_PROP_PROTOCOL, "http");
		assertEquals("http://myhost.com", YacyDefaultServlet.getContext(header, null));
		
		header = new RequestHeader();
		header.put(HeaderFramework.HOST, "myhost.com");
		header.put(HeaderFramework.CONNECTION_PROP_PROTOCOL, "https");
		assertEquals("https://myhost.com", YacyDefaultServlet.getContext(header, null));
	}
	
	/**
	 * getContext() : reverse proxy serving HTTPS, Yacy serving HTTP
	 */
    @Test
    @SuppressWarnings("deprecation")
	public void testGetContextReverseProxy() {
		/* Different protocols : HTTPS on proxy, HTTP on peer */
		RequestHeader header = new RequestHeader();
		header.put(HeaderFramework.HOST, "myhost.com");
		header.put(HeaderFramework.CONNECTION_PROP_PROTOCOL, "http");
		header.put(HttpHeaders.X_FORWARDED_PROTO.toString(), "https");
		assertEquals("https://myhost.com", YacyDefaultServlet.getContext(header, null));
		
		/* Illegal X-Forwarded-Proto header value */
		header = new RequestHeader();
		header.put(HeaderFramework.HOST, "myhost.com:8090");
		header.put(HeaderFramework.CONNECTION_PROP_PROTOCOL, "http");
		header.put(HttpHeaders.X_FORWARDED_PROTO.toString(), "http://attacker.com?query=");
		assertEquals("http://myhost.com:8090", YacyDefaultServlet.getContext(header, null));
	}
	
	/**
	 * Tests on getRelativeBase()
	 */
	@Test
	public void testGetRelativeBase() {
		assertEquals("", YacyDefaultServlet.getRelativeBase(null));
		assertEquals("", YacyDefaultServlet.getRelativeBase(""));
		assertEquals("", YacyDefaultServlet.getRelativeBase("/"));
		assertEquals("", YacyDefaultServlet.getRelativeBase("/file.html"));
		assertEquals("", YacyDefaultServlet.getRelativeBase("file.html"));
		assertEquals("", YacyDefaultServlet.getRelativeBase("resource"));
		assertEquals("../", YacyDefaultServlet.getRelativeBase("folder/file.html"));
		assertEquals("../", YacyDefaultServlet.getRelativeBase("folder/resource"));
		assertEquals("../", YacyDefaultServlet.getRelativeBase("/folder/resource"));
		assertEquals("../", YacyDefaultServlet.getRelativeBase("a/b"));
		assertEquals("../../", YacyDefaultServlet.getRelativeBase("folder/subfolder/resource"));
		assertEquals("../../", YacyDefaultServlet.getRelativeBase("/folder/subfolder/resource"));
		assertEquals("../", YacyDefaultServlet.getRelativeBase("folder/"));
		assertEquals("../../", YacyDefaultServlet.getRelativeBase("folder/subfolder/"));
	}
	

}
