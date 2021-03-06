/**
 * Copyright (C) FuseSource, Inc.
 * http://fusesource.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.fabric.bridge.internal;

import javax.jms.ConnectionFactory;

import junit.framework.Assert;
import org.apache.activemq.pool.AmqJNDIPooledConnectionFactory;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ConnectionFactoryAdapterTest extends Assert {
	
	private AmqJNDIPooledConnectionFactory connectionFactory;
	private ConnectionFactoryAdapter adapter;
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactoryAdapterTest.class);
    private static final String TEST_LOCAL_BROKER_URL = "vm://localhost?broker.persistent=false";

    @Before
	public void setUp() throws Exception {
		connectionFactory = new AmqJNDIPooledConnectionFactory(TEST_LOCAL_BROKER_URL);
		adapter = new ConnectionFactoryAdapter();
	}

	@After
	public void tearDown() throws Exception {
		connectionFactory.stop();
		connectionFactory = null;
		adapter = null;
	}

	@Test
	public void testMarshalConnectionFactory() throws Exception {
		byte[] bytes = adapter.marshal(connectionFactory);
		String str = new String(Base64.encodeBase64(bytes));
		LOG.info("Marshaled ConnectionFactory bytes base64 encoded" + System.getProperty("line.separator") + str);
		assertNotNull("Null marshaled bytes", bytes);
		assertFalse("Empty marshaled bytes", bytes.length == 0);
	}
	
	@Test
	public void testUnmarshalByteArray() throws Exception {
		byte[] bytes = adapter.marshal(connectionFactory);
		ConnectionFactory newConnectionFactory = adapter.unmarshal(bytes);
		assertNotNull("Connection factory is null", newConnectionFactory);
	}

}
