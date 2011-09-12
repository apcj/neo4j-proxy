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
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.eventmodel.parameters.Parameter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PlaybackDriver {

    public interface Listener {
        void beforePlayback(Event event);
        void afterPlayback(Event event);
        void playbackException(Event event, Exception exception);
    }

    private PlaybackState playbackState;

    public PlaybackDriver(GraphDatabaseService graphDatabase) {
        playbackState = new PlaybackState(graphDatabase);
    }

    public void playback(Iterable<Event> events) {
        playback(events, new HaltOnException());
    }

    public void playback(Iterable<Event> events, Listener listener) {
        for (Event event : events) {
            try {
                listener.beforePlayback(event);

                Object target = event.getTarget().getValueForPlayback(playbackState);
                Object[] arguments = decodeParameters(event.getParameters());
                Method method = deduceMethod(event, event.getTarget().getType().getWrappedType(), arguments);

                Object result = method.invoke(target, arguments);
                playbackState.capture(result);

                listener.afterPlayback(event);
            } catch (InvocationTargetException e) {
                listener.playbackException(event, e);
            } catch (IllegalAccessException e) {
                listener.playbackException(event, e);
            } catch (RuntimeException e) {
                listener.playbackException(event, e);
            }
        }
    }

    private Object[] decodeParameters(Parameter[] parameters) {
        Object[] decodedParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            decodedParameters[i] = parameters[i].getValueForPlayback(playbackState);
        }
        return decodedParameters;
    }

    private Method deduceMethod(Event event, Class targetClass, Object[] arguments) {
        for (Method candidateMethod : targetClass.getMethods()) {
            if (candidateMethod.getName().equals(event.getMethodName())
                    && areCompatible(candidateMethod.getParameterTypes(), arguments)) {
                return candidateMethod;
            }
        }
        throw new IllegalArgumentException(String.format("no suitable method named %s on class %s",
                event.getMethodName(), targetClass));
    }

    private boolean areCompatible(Class<?>[] parameterTypes, Object[] arguments) {
        if (parameterTypes.length != arguments.length) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].isAssignableFrom(arguments[i].getClass())) {
                return false;
            }
        }
        return true;
    }

    public static class HaltOnException implements Listener {
        public void beforePlayback(Event event) {
        }

        public void afterPlayback(Event event) {
        }

        public void playbackException(Event event, Exception e) {
            throw new RuntimeException(e);
        }
    }
}
