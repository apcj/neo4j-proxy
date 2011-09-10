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
import org.neo4j.graphdb.Traverser;
import org.neo4j.proxy.eventmodel.parameters.Parameter;
import org.neo4j.proxy.eventmodel.parameters.ParameterFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.neo4j.proxy.eventmodel.serialization.JacksonAdaptor.parseParameter;
import static org.neo4j.proxy.eventmodel.serialization.JacksonAdaptor.serializeParameter;

public class ParameterFactoryTest {

    ParameterFactory factory = new ParameterFactory();

    @Test
    public void shouldAcceptApiEnums()
    {
        assertCanRoundTrip(Direction.OUTGOING);
        assertCanRoundTrip(Traverser.Order.BREADTH_FIRST);
    }

    @Test
    public void shouldAcceptNull()
    {
        assertCanRoundTrip(null);
    }

    @Test
    public void shouldAcceptAllPrimitiveTypes()
    {
        assertCanRoundTrip(true);
        assertCanRoundTrip(Byte.MAX_VALUE);
        assertCanRoundTrip(23);
        assertCanRoundTrip(12l);
        assertCanRoundTrip(2.3f);
        assertCanRoundTrip(3.4d);
        assertCanRoundTrip("I am a String");
    }

    @Test
    public void shouldAcceptArrays()
    {
        assertCanRoundTripArray(new boolean[]{true, false, true});
        assertCanRoundTripArray(new Boolean[]{true, false, true});
        assertCanRoundTripArray(new byte[]{Byte.MAX_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE});
        assertCanRoundTripArray(new Byte[]{Byte.MAX_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE});
        assertCanRoundTripArray(new int[]{1, 2, 3});
        assertCanRoundTripArray(new Integer[]{1, 2, 3});
        assertCanRoundTripArray(new Long[]{1l, 2l, 3l});
        assertCanRoundTripArray(new Float[]{1.0f, 2.0f, 3.0f});
        assertCanRoundTripArray(new Double[]{1.0d, 2.0d, 3.0d});
        assertCanRoundTripArray(new Double[]{1.0d, 2.0d, 3.0d});
        assertCanRoundTripArray(new String[]{"These", "are", "strings"});
    }

    static class BothIterableAndIterator implements Iterable, Iterator {

        public Iterator iterator() {
            return null;
        }

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            return null;
        }

        public void remove() {
        }
    }

    @Test
    public void shouldKeepTrackOfIterablesAndIteratorsViaSurrogateIdentifiers()
    {
        Parameter iterableParameter = factory.fromObjectWithSpecificType(new BothIterableAndIterator(), Iterable.class);
        Parameter iteratorParameter = factory.fromObjectWithSpecificType(new BothIterableAndIterator().iterator(), Iterator.class);
        assertEquals("Iterable", iterableParameter.getType().getWrappedType().getSimpleName());
        assertEquals(0, iterableParameter.getValueForSerialization());
        assertEquals("Iterator", iteratorParameter.getType().getWrappedType().getSimpleName());
        assertEquals(0, iteratorParameter.getValueForSerialization());
    }

    private void assertCanRoundTrip(Object object) {
        assertEquals(object, factory.fromObject(object).getValueForPlayback(null));
        assertEquals(object, parseParameter(serializeParameter(factory.fromObject(object))).getValueForPlayback(null));
    }

    private void assertCanRoundTripArray(boolean[] array) {
        assertTrue(Arrays.equals(array, (boolean[]) factory.fromObject(array).getValueForPlayback(null)));
        assertTrue(Arrays.equals(array, (boolean[]) parseParameter(serializeParameter(factory.fromObject(array))).getValueForPlayback(null)));
    }

    private void assertCanRoundTripArray(byte[] array) {
        assertArrayEquals(array, (byte[]) factory.fromObject(array).getValueForPlayback(null));
        assertArrayEquals(array, (byte[]) parseParameter(serializeParameter(factory.fromObject(array))).getValueForPlayback(null));
    }

    private void assertCanRoundTripArray(int[] array) {
        assertArrayEquals(array, (int[]) factory.fromObject(array).getValueForPlayback(null));
        assertArrayEquals(array, (int[]) parseParameter(serializeParameter(factory.fromObject(array))).getValueForPlayback(null));
    }

    private void assertCanRoundTripArray(Object[] array) {
        assertArrayEquals(array, (Object[]) factory.fromObject(array).getValueForPlayback(null));
        assertArrayEquals(array, (Object[]) parseParameter(serializeParameter(factory.fromObject(array))).getValueForPlayback(null));
    }
}
