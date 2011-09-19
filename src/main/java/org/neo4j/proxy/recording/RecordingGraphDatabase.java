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
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.eventmodel.parameter.Parameter;
import org.neo4j.proxy.eventmodel.parameter.ParameterFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

public class RecordingGraphDatabase {

    public static GraphDatabaseService create(final Event.Listener listener, final GraphDatabaseService delegate) {
        final Event.Listener filteredListener = new FilterOutUninterestingMethods(listener);
        ParameterFactory parameterFactory = new ParameterFactory();
        return createProxy(filteredListener, delegate, parameterFactory.fromObject(delegate), GraphDatabaseService.class, parameterFactory);
    }

    public static <T> T createProxy(final Event.Listener listener, final T delegate, final Parameter targetParameter, final Class aClass, final ParameterFactory parameterFactory) {

        //noinspection unchecked
        return (T) Proxy.newProxyInstance(RecordingGraphDatabase.class.getClassLoader(), new Class[]{aClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {

                final Object result = method.invoke(delegate, arguments);
                try {
                    if (result instanceof Node || result instanceof Relationship || result instanceof Transaction
                            || result instanceof Iterable || result instanceof Iterator) {
                        Class<?> proxyInterface = chooseProxyInterface(method, result);
                        final Parameter resultParameter = parameterFactory.fromObjectWithSpecificType(result, proxyInterface);
                        listener.onEvent(new Event(targetParameter, method.getName(), convert(arguments, parameterFactory), resultParameter));
                        return createProxy(listener, result, resultParameter, proxyInterface, parameterFactory);
                    } else {
                        final Parameter resultParameter = parameterFactory.fromObject(result);
                        listener.onEvent(new Event(targetParameter, method.getName(), convert(arguments, parameterFactory), resultParameter));
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                return result;
            }
        });
    }

    private static Class<?> chooseProxyInterface(Method method, Object result) {

        if (method.getReturnType().isInterface()) return method.getReturnType();
        if (result instanceof Node) return Node.class;
        if (result instanceof Relationship) return Relationship.class;

        throw new IllegalArgumentException("No suitable interface for " + result.getClass());
    }

    private static Parameter[] convert(Object[] arguments, ParameterFactory parameterFactory) {
        if (arguments == null) return new Parameter[0];
        Parameter[] detachedArguments = new Parameter[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            detachedArguments[i] = parameterFactory.fromObject(arguments[i]);
        }
        return detachedArguments;
    }

    private static class FilterOutUninterestingMethods implements Event.Listener {
        private Event.Listener delegate;

        public FilterOutUninterestingMethods(Event.Listener delegate) {
            this.delegate = delegate;
        }

        public void onEvent(Event event) {
            if ("toString".equals(event.getMethodName())) return;
            delegate.onEvent(event);
        }
    }
}
