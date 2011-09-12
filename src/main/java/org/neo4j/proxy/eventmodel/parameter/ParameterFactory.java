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
package org.neo4j.proxy.eventmodel.parameter;

import org.neo4j.graphdb.*;
import org.neo4j.proxy.eventmodel.parameter.types.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParameterFactory {

    public final List<ParameterType> types = new ArrayList<ParameterType>();

    public ParameterFactory() {
        types.add(new NullParameterType());
        types.add(new GraphDatabaseServiceParameterType());
        types.add(new TransactionParameterType());
        types.add(new NodeParameterType());
        types.add(new RelationshipParameterType());
        types.add(new RelationshipTypeParameterType());
        types.add(new RelationshipTypeArrayParameterType());
        types.add(new SurrogateIdentifierParameterType(Iterator.class));
        types.add(new SurrogateIdentifierParameterType(Iterable.class));

        types.add(new EnumParameterType(Direction.class));
        types.add(new EnumParameterType(Traverser.Order.class));

        types.add(new PrimitiveParameterType(Boolean.class));
        types.add(new PrimitiveParameterType(Byte.class));
        types.add(new PrimitiveParameterType(Integer.class));
        types.add(new PrimitiveParameterType(Long.class));
        types.add(new PrimitiveParameterType(Float.class));
        types.add(new PrimitiveParameterType(Double.class));
        types.add(new PrimitiveParameterType(String.class));

        types.add(new PrimitiveParameterType(boolean[].class));
        types.add(new PrimitiveParameterType(Boolean[].class));
        types.add(new PrimitiveParameterType(byte[].class));
        types.add(new PrimitiveParameterType(Byte[].class));
        types.add(new PrimitiveParameterType(int[].class));
        types.add(new PrimitiveParameterType(Integer[].class));
        types.add(new PrimitiveParameterType(long[].class));
        types.add(new PrimitiveParameterType(Long[].class));
        types.add(new PrimitiveParameterType(float[].class));
        types.add(new PrimitiveParameterType(Float[].class));
        types.add(new PrimitiveParameterType(double[].class));
        types.add(new PrimitiveParameterType(Double[].class));
        types.add(new PrimitiveParameterType(String[].class));
    }

    public Parameter fromObject(Object argument) {
        for (ParameterType type : types) {
            if (type.acceptObject(argument)) {
                return type.fromObject(argument);
            }
        }
        throw new IllegalArgumentException("Cannot accept type of argument: " + argument.getClass());
    }

    public Parameter fromObjectWithSpecificType(Object argument, Class interfaceType) {
        for (ParameterType type : types) {
            if (type.getWrappedType() == interfaceType) {
                return type.fromObject(argument);
            }
        }
        throw new IllegalArgumentException("Cannot accept type of argument: " + interfaceType);
    }

}
