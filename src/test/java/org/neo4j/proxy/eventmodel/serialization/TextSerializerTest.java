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
import org.neo4j.proxy.eventmodel.Parameter;
import org.neo4j.proxy.eventmodel.PrimitiveValue;

import java.io.PrintWriter;

import static org.mockito.Mockito.*;

public class TextSerializerTest {

    @Test
    public void shouldPrintEachEvent() throws Exception {
        PrintWriter printWriter = mock(PrintWriter.class);

        Event event = new Event("target", "method", new Parameter[] {new PrimitiveValue(PrimitiveValue.SupportedTypes.String, "name"), new PrimitiveValue(PrimitiveValue.SupportedTypes.String, "Alistair")});
        new TextSerializer(printWriter).onEvent(event);

        verify(printWriter).println(event.toString());
    }

    @Test
    public void shouldFlushOnClose()
    {
        PrintWriter printWriter = mock(PrintWriter.class);

        new TextSerializer(printWriter).flush();

        verify(printWriter).flush();
    }
}
