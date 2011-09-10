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

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.proxy.eventmodel.parameters.Parameter;
import org.neo4j.proxy.eventmodel.serialization.JacksonAdaptor;

import java.util.Arrays;

public class Event {

    public interface Listener {
        void onEvent(Event event);
    }

    private Parameter target;
    private String methodName;
    private Parameter[] parameters;

    public Event(Parameter target, String methodName, Parameter[] parameters) {
        this.target = target;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public Parameter getTarget() {
        return target;
    }

    public String getMethodName() {
        return methodName;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public String toString() {
        return JacksonAdaptor.serializeEvent(this).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (methodName != null ? !methodName.equals(event.methodName) : event.methodName != null) return false;
        if (!Arrays.equals(parameters, event.parameters)) return false;
        if (target != null ? !target.equals(event.target) : event.target != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (parameters != null ? Arrays.hashCode(parameters) : 0);
        return result;
    }

}
