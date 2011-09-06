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
package org.neo4j.proxy.playback;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.proxy.eventmodel.DetachedNode;
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.eventmodel.GraphEntity;
import org.neo4j.proxy.eventmodel.Parameter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.neo4j.proxy.playback.NodeCache.nodeId;

public class PlaybackDriver {
    private GraphDatabaseService graphDatabase;
    private NodeCache nodeCache = new NodeCache();
    private Transaction currentTransaction = null;

    public PlaybackDriver(GraphDatabaseService graphDatabase) {
        this.graphDatabase = graphDatabase;
    }

    public void playback(Iterable<Event> events) {
        for (Event event : events) {
            Class<? extends GraphDatabaseService> graphDatabaseClass = GraphDatabaseService.class;
            if (event.getTarget().equals(graphDatabaseClass.getSimpleName())) {
                play(event, graphDatabase, graphDatabaseClass);
            } else if (event.getTarget().startsWith("Node")) {
                play(event, nodeCache.get(nodeId(event.getTarget())), Node.class);
            } else if (event.getTarget().startsWith("Transaction")) {
                play(event, currentTransaction, Transaction.class);
            }
        }
    }

    private <T> void play(Event event, T target, Class<? extends T> targetClass) {
        try {
            capture(deduceMethod(event, targetClass).invoke(target, decodeParameters(event.getParameters())));
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] decodeParameters(Parameter[] parameters) {
        Object[] decodedParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            decodedParameters[i] = parameters[i].getValue(nodeCache);
        }
        return decodedParameters;
    }

    private void capture(Object result) {
        if (result instanceof Node) {
            nodeCache.put((Node) result);
        }
        if (result instanceof Transaction) {
            currentTransaction = (Transaction) result;
        }
    }

    private Method deduceMethod(Event event, Class targetClass) {
        for (Method candidateMethod : targetClass.getMethods()) {
            if (candidateMethod.getName().equals(event.getMethodName())) {
                return candidateMethod;
            }
        }
        throw new IllegalArgumentException(String.format("no suitable method named %s on class %s", event.getMethodName(), targetClass));
    }

}
