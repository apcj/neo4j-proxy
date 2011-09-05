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

public class Event {
    private String target;
    private String methodName;
    private Object[] parameters;

    public Event(String target, String methodName, Object[] parameters) {
        this.target = target;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getTarget() {
        return target;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(target).append(" ").append(methodName);
        for (Object parameter : parameters) {
            builder.append(" ").append(parameter);
        }
        return builder.toString();
    }

}
