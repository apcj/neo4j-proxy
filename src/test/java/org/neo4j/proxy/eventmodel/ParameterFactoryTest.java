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
import org.neo4j.graphdb.Direction;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.neo4j.proxy.eventmodel.ParameterFactory.fromObject;
import static org.neo4j.proxy.eventmodel.serialization.ParameterStringAdaptor.parse;
import static org.neo4j.proxy.eventmodel.serialization.ParameterStringAdaptor.serialize;

public class ParameterFactoryTest {

    @Test
    public void shouldAcceptDirection()
    {
        assertCanRoundTrip(Direction.OUTGOING);
    }

    @Test
    public void shouldAcceptArrays()
    {
        assertCanRoundTripArray(new int[]{});
        assertCanRoundTripArray(new int[]{1, 2, 3});
//        assertCanRoundTripArray(new Integer[]{1, 2, 3});
    }

    private void assertCanRoundTrip(Object object) {
        assertEquals(object, fromObject(object).getValue(null));
        assertEquals(object, parse(serialize(fromObject(object))).getValue(null));
    }

    private void assertCanRoundTripArray(int[] array) {
        assertArrayEquals(array, (int[]) fromObject(array).getValue(null));
        assertArrayEquals(array, (int[]) parse(serialize(fromObject(array))).getValue(null));
    }

    private void assertCanRoundTripArray(Object[] array) {
        assertArrayEquals(array, (Object[]) fromObject(array).getValue(null));
        assertArrayEquals(array, (Object[]) parse(serialize(fromObject(array))).getValue(null));
    }
}
