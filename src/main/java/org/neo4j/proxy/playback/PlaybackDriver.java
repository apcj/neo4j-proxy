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
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.eventmodel.GraphEntity;
import org.neo4j.proxy.eventmodel.Parameter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PlaybackDriver {
    private GraphDatabaseService graphDatabase;
    private PlaybackState playbackState;

    public PlaybackDriver(GraphDatabaseService graphDatabase) {
        this.graphDatabase = graphDatabase;
        playbackState = new PlaybackState(graphDatabase);
    }

    public void playback(Iterable<Event> events) {
        for (Event event : events) {
            Class<? extends GraphDatabaseService> graphDatabaseClass = GraphDatabaseService.class;
            if (event.getTarget().getKind() == GraphEntity.Kinds.GraphDatabaseService) {
                play(event, graphDatabase, graphDatabaseClass);
            } else if (event.getTarget().getKind() == GraphEntity.Kinds.Node) {
                play(event, event.getTarget().getValue(playbackState), Node.class);
            } else if (event.getTarget().getKind() == GraphEntity.Kinds.Transaction) {
                play(event, event.getTarget().getValue(playbackState), Transaction.class);
            }
        }
    }

    private <T> void play(Event event, T target, Class<? extends T> targetClass) {
        try {
            Object result = deduceMethod(event, targetClass).invoke(target, decodeParameters(event.getParameters()));
            playbackState.capture(result);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] decodeParameters(Parameter[] parameters) {
        Object[] decodedParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            decodedParameters[i] = parameters[i].getValue(playbackState);
        }
        return decodedParameters;
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
