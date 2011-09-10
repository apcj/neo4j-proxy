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
package org.neo4j.proxy;

import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.eventmodel.FakeNode;
import org.neo4j.proxy.recording.RecordingGraphDatabase;
import sun.org.mozilla.javascript.internal.ObjToIntMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IteratorsTest {

    @Test
    public void shouldHandleIterators()
    {
        GraphDatabaseService wrapped = mock(GraphDatabaseService.class);
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(new FakeNode(1));
        nodes.add(new FakeNode(2));

        when(wrapped.getAllNodes()).thenReturn(nodes);

        ArrayList<Event> events = new ArrayList<Event>();
        GraphDatabaseService service = RecordingGraphDatabase.create(new EventListAccumulator(events), wrapped);

        for (Node node : service.getAllNodes()) {
            node.setProperty("name", "Alistair");
        }

        for (Event event : events) {
            System.out.println("event = " + event);
        }
    }

    @Test
    public void types() throws NoSuchMethodException {
        Method method = new Iterator<Node>() {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Node next() {
                return null;
            }

            @Override
            public void remove() {
            }
        }.getClass().getMethod("next");
        System.out.println("method.getName() = " + method.getName());
        System.out.println("method.getReturnType() = " + method.getReturnType());
        System.out.println("method.getGenericReturnType() = " + method.getGenericReturnType());

        Method getReferenceNode = GraphDatabaseService.class.getMethod("getReferenceNode");
        System.out.println("getReferenceNode.getGenericReturnType() = " + getReferenceNode.getGenericReturnType());
    }
}
