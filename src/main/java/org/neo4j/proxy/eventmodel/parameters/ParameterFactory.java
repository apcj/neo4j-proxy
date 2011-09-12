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
package org.neo4j.proxy.eventmodel.parameters;

import org.neo4j.graphdb.*;
import org.neo4j.proxy.eventmodel.EntityFinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParameterFactory {

    public final List<ParameterType> types = new ArrayList<ParameterType>();

    public ParameterFactory() {
        types.add(new NullParameterType());
        types.add(new BaseParameterType(GraphDatabaseService.class, String.class) {

            public Object getValueForPlayback(Object serializedValue, EntityFinder entityFinder) {
                return entityFinder.getGraphDatabase();
            }

            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new Parameter(this, "");
            }

            public Parameter fromObject(Object entity) {
                return new Parameter(this, "");
            }
        });
        types.add(new BaseParameterType(Transaction.class, String.class) {

            public Object getValueForPlayback(Object serializedValue, EntityFinder entityFinder) {
                return entityFinder.getCurrentTransaction();
            }

            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new Parameter(this, "");
            }

            public Parameter fromObject(Object entity) {
                return new Parameter(this, "");
            }
        });
        types.add(new BaseParameterType(Node.class, long.class) {

            public Object getValueForPlayback(Object serializedValue, EntityFinder entityFinder) {
                return entityFinder.getNode((Long) serializedValue);
            }

            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new Parameter(this, serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new Parameter(this, ((Node) entity).getId());
            }
        });
        types.add(new BaseParameterType(Relationship.class, long.class) {

            public Object getValueForPlayback(Object serializedValue, EntityFinder entityFinder) {
                return entityFinder.getRelationship((Long) serializedValue);
            }

            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new Parameter(this, serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new Parameter(this, ((Relationship) entity).getId());
            }
        });
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

    private class RelationshipTypeParameterType extends BaseParameterType {

        public RelationshipTypeParameterType() {
            super(RelationshipType.class, String.class);
        }

        public Object getValueForPlayback(Object serializedValue, EntityFinder entityFinder) {
            return new StandInRelationshipType((String) serializedValue);
        }

        public Parameter fromSerializedValue(String typeString, Object serializedValue) {
            return new Parameter(this, serializedValue);
        }

        public Parameter fromObject(Object entity) {
            return new Parameter(this, ((RelationshipType) entity).name());
        }
    }

    private class RelationshipTypeArrayParameterType extends BaseParameterType {

        public RelationshipTypeArrayParameterType() {
            super(RelationshipType[].class, String[].class);
        }

        public Object getValueForPlayback(Object serializedValue, EntityFinder entityFinder) {
            String[] names = (String[]) serializedValue;
            RelationshipType[] relationshipTypes = new RelationshipType[names.length];
            for (int i = 0; i < names.length; i++) {
                relationshipTypes[i] = new StandInRelationshipType(names[i]);
            }
            return relationshipTypes;
        }

        public Parameter fromSerializedValue(String typeString, Object serializedValue) {
            return new Parameter(this, (String[]) serializedValue);
        }

        public Parameter fromObject(Object entity) {
            RelationshipType[] relationshipTypes = (RelationshipType[]) entity;
            String[] names = new String[relationshipTypes.length];
            for (int i = 0; i < relationshipTypes.length; i++) {
                names[i] = relationshipTypes[i].name();
            }
            return new Parameter(this, names);
        }
    }

    private class StandInRelationshipType implements RelationshipType {
        private String name;

        public StandInRelationshipType(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }
    }
}
