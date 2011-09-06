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
package org.neo4j.proxy.eventmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventTest {

    @Test
    public void shouldFormatForReadability()
    {
        Event event = new Event(new GraphEntity(GraphEntity.Kinds.Node, 20), "setProperty", new Parameter[] {new PrimitiveValue(PrimitiveValue.SupportedTypes.String, "name"), new PrimitiveValue(PrimitiveValue.SupportedTypes.String, "Alistair")});
        assertEquals("Node(20) setProperty String(\"name\") String(\"Alistair\")", event.toString());
    }

    @Test
    public void shouldParseFromString()
    {
        Event event = Event.parse("Node(20) setProperty String(\"name\") String(\"Alistair\")");
        assertEquals(2, event.getParameters().length);
    }
}
