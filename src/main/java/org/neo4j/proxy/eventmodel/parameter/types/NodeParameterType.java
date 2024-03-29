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
package org.neo4j.proxy.eventmodel.parameter.types;

import org.neo4j.graphdb.Node;
import org.neo4j.proxy.eventmodel.EntityFinder;
import org.neo4j.proxy.eventmodel.parameter.Parameter;

public class NodeParameterType extends ParameterType {

    public NodeParameterType() {
        super(Node.class, long.class);
    }

    public Object getValueForPlayback(Object serializedValue, EntityFinder entityFinder) {
        return entityFinder.getNode((Long) serializedValue);
    }

    public Parameter fromSerializedValue(String typeString, Object serializedValue) {
        return new Parameter(this, serializedValue);
    }

    public Parameter fromObject(Object entity) {
        return new Parameter(this, ((Node) entity).getId());
    }
}
