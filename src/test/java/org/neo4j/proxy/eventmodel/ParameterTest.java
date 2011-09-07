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
import org.neo4j.graphdb.Node;
import org.neo4j.proxy.eventmodel.serialization.ParameterStringAdaptor;

import static org.junit.Assert.assertEquals;

public class ParameterTest {

    @Test
    public void shouldParseString() throws Exception {
        Parameter parameter = ParameterStringAdaptor.parse("String(\"name\")");
        assertEquals("name", parameter.getValue(null));
    }

    @Test
    public void shouldParseInteger() throws Exception {
        Parameter parameter = ParameterStringAdaptor.parse("Integer(42)");
        assertEquals(42, parameter.getValue(null));
    }

    @Test
    public void shouldParseNode() throws Exception {
        Parameter parameter = ParameterStringAdaptor.parse("Node(13)");
        assertEquals(Node.class, parameter.getType().getWrappedType());
    }

    @Test
    public void shouldParseRelationshipType() throws Exception {
        Parameter parameter = ParameterStringAdaptor.parse("RelationshipType(\"KNOWS\")");
        assertEquals("KNOWS", ((org.neo4j.graphdb.RelationshipType) parameter.getValue(null)).name());
    }
}
