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
import org.neo4j.graphdb.Transaction;
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.eventmodel.Parameter;
import org.neo4j.proxy.eventmodel.ParameterFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RecordingGraphDatabase {

    public interface Listener {
        void onEvent(Event event);
    }

    public static GraphDatabaseService create(final Listener listener, final GraphDatabaseService delegate) {
        final Listener filteredListener = new FilterOutUninterestingMethods(listener);
        return createProxy(filteredListener, delegate, GraphDatabaseService.class);
    }

    public static <T> T createProxy(final Listener listener, final T delegate, final Class aClass) {
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(RecordingGraphDatabase.class.getClassLoader(), new Class[]{aClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
                System.out.println("method.getName() = " + method.getName());
                listener.onEvent(new Event(ParameterFactory.fromObject(delegate), method.getName(), convert(arguments)));
                Object result = method.invoke(delegate, arguments);
                if (result instanceof Node || result instanceof Transaction) {
                    return createProxy(listener, result, method.getReturnType());
                }
                return result;
            }
        });
    }

    private static Parameter[] convert(Object[] arguments) {
        if (arguments == null) return new Parameter[0];
        Parameter[] detachedArguments = new Parameter[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            detachedArguments[i] = ParameterFactory.fromObject(arguments[i]);
        }
        return detachedArguments;
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
