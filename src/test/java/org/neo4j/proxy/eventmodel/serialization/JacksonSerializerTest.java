/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.proxy.eventmodel.serialization;

import org.junit.Test;
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.eventmodel.parameter.Parameter;
import org.neo4j.proxy.eventmodel.parameter.ParameterFactory;

import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.neo4j.proxy.eventmodel.FakeNode.node;
import static org.neo4j.proxy.eventmodel.serialization.JacksonAdaptor.parseEvent;
import static org.neo4j.proxy.eventmodel.serialization.JacksonAdaptor.serializeEvent;

public class JacksonSerializerTest {

    ParameterFactory factory = new ParameterFactory();

    @Test
    public void canRoundTripViaJson()
    {
        Event event = new Event(factory.fromObject(node(20)), "method", new Parameter[] {factory.fromObject("name"), factory.fromObject("Alistair")}, factory.fromObject(null));

        assertEquals(event, parseEvent(serializeEvent(event)));

        System.out.println("serializeEvent(event) = " + serializeEvent(event));
    }

    @Test
    public void shouldPrintEachEvent() throws Exception {
        PrintWriter printWriter = mock(PrintWriter.class);

        Event event = new Event(factory.fromObject(node(20)), "method", new Parameter[] {factory.fromObject("name"), factory.fromObject("Alistair")}, factory.fromObject(null));
        new JacksonSerializer(printWriter).onEvent(event);

        verify(printWriter).println(anyString());
    }

    @Test
    public void shouldFlushOnClose()
    {
        PrintWriter printWriter = mock(PrintWriter.class);

        new JacksonSerializer(printWriter).flush();

        verify(printWriter).flush();
    }

}
