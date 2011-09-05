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
package org.neo4j.proxy.recording;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.proxy.eventmodel.DetachedNode;
import org.neo4j.proxy.eventmodel.Event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RecordingGraphDatabase {

    public interface Listener {
        void onEvent(Event event);
    }

    public static GraphDatabaseService create(final Listener listener, final GraphDatabaseService delegate) {
        final Listener filteredListener = new FilterOutUninterestingMethods(listener);
        return createRecordingNode(filteredListener, delegate, GraphDatabaseService.class);
    }

    public static <T> T createRecordingNode(final Listener listener, final T delegate, final Class aClass) {
        final String target = describe(delegate, aClass);
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(RecordingGraphDatabase.class.getClassLoader(), new Class[]{aClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object[] arguments = args == null ? new Object[0] : args;
                listener.onEvent(new Event(target, method.getName(), detachArguments(arguments)));
                Object result = method.invoke(delegate, args);
                if (result instanceof Node || result instanceof Transaction) {
                    return createRecordingNode(listener, result, method.getReturnType());
                }
                return result;
            }
        });
    }

    private static Object[] detachArguments(Object[] arguments) {
        Object[] detachedArguments = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            detachedArguments[i] = detachArgument(arguments[i]);
        }
        return detachedArguments;
    }

    private static Object detachArgument(Object argument) {
        if (argument instanceof Node) {
            return new DetachedNode(((Node) argument).getId());
        }
        return argument;
    }

    private static <T> String describe(T delegate, Class aClass) {
        if (delegate instanceof Node) {
            return "Node[" + ((Node) delegate).getId() + "]";
        }
        if (delegate instanceof Relationship) {
            return "Relationship[" + ((Relationship) delegate).getId() + "]";
        }
        return aClass.getSimpleName();
    }

    private static class FilterOutUninterestingMethods implements Listener {
        private Listener delegate;

        public FilterOutUninterestingMethods(Listener delegate) {
            this.delegate = delegate;
        }

        public void onEvent(Event event) {
            if ("toString".equals(event.getMethodName())) return;
            delegate.onEvent(event);
        }
    }
}
