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

public class ParameterTest {

    @Test
    public void shouldParseString() throws Exception {
        PrimitiveValue parameter = (PrimitiveValue) Parameter.parse("String(\"name\")");
        assertEquals(PrimitiveValue.SupportedTypes.String, parameter.getType());
        assertEquals("name", parameter.getValue());
    }

    @Test
    public void shouldParseInteger() throws Exception {
        PrimitiveValue parameter = (PrimitiveValue) Parameter.parse("Integer(42)");
        assertEquals(PrimitiveValue.SupportedTypes.Integer, parameter.getType());
        assertEquals(42, parameter.getValue());
    }

    @Test
    public void shouldParseNode() throws Exception {
        GraphEntity parameter = (GraphEntity) Parameter.parse("Node(13)");
        assertEquals(GraphEntity.Kinds.Node, parameter.getKind());
        assertEquals(13, parameter.getId());
    }

    @Test
    public void shouldParseRelationshipType() throws Exception {
        RelationshipType parameter = (RelationshipType) Parameter.parse("RelationshipType(\"KNOWS\")");
        assertEquals("KNOWS", parameter.name());
    }
}
