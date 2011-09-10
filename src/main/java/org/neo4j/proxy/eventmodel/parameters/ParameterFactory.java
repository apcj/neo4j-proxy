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
        types.add(new BaseParameterType(GraphDatabaseService.class) {
            public Class getSerializedType() {
                return String.class;
            }

            class GraphDatabaseServiceParameter extends BaseParameter {
                protected GraphDatabaseServiceParameter(ParameterType type) {
                    super(type);
                }

                public Object getValueForPlayback(EntityFinder entityFinder) {
                    return entityFinder.getGraphDatabase();
                }

                public Object getValueForSerialization() {
                    return "";
                }
            }

            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new GraphDatabaseServiceParameter(this);
            }

            public Parameter fromObject(Object entity) {
                return new GraphDatabaseServiceParameter(this);
            }
        });
        types.add(new BaseParameterType(Transaction.class) {
            public Class getSerializedType() {
                return String.class;
            }

            class TransactionParameter extends BaseParameter {
                protected TransactionParameter(ParameterType type) {
                    super(type);
                }

                public Object getValueForPlayback(EntityFinder entityFinder) {
                    return entityFinder.getCurrentTransaction();
                }

                public Object getValueForSerialization() {
                    return "";
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new TransactionParameter(this);
            }

            public Parameter fromObject(Object entity) {
                return new TransactionParameter(this);
            }
        });
        types.add(new BaseParameterType(Node.class) {

            public Class getSerializedType() {
                return long.class;
            }

            class NodeParameter extends BaseParameter {
                private long id;

                public NodeParameter(ParameterType type, long id) {
                    super(type);
                    this.id = id;
                }

                public Object getValueForPlayback(EntityFinder entityFinder) {
                    return entityFinder.getNode(id);
                }

                public Object getValueForSerialization() {
                    return id;
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new NodeParameter(this, (Long) serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new NodeParameter(this, ((Node) entity).getId());
            }
        });
        types.add(new BaseParameterType(Relationship.class) {

            public Class getSerializedType() {
                return long.class;
            }

            class RelationshipParameter extends BaseParameter {
                private long id;

                public RelationshipParameter(ParameterType type, long id) {
                    super(type);
                    this.id = id;
                }

                public Object getValueForPlayback(EntityFinder entityFinder) {
                    return entityFinder.getRelationship(id);
                }

                public Object getValueForSerialization() {
                    return id;
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new RelationshipParameter(this, (Long) serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new RelationshipParameter(this, ((Relationship) entity).getId());
            }
        });
        types.add(new BaseParameterType(RelationshipType.class) {

            public Class getSerializedType() {
                return String.class;
            }

            class RelationshipTypeParameter extends BaseParameter implements RelationshipType {
                private String name;

                RelationshipTypeParameter(ParameterType type, String name) {
                    super(type);
                    this.name = name;
                }

                public Object getValueForPlayback(EntityFinder entityFinder) {
                    return this;
                }

                public String name() {
                    return name;
                }

                public Object getValueForSerialization() {
                    return name;
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new RelationshipTypeParameter(this, (String) serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new RelationshipTypeParameter(this, ((org.neo4j.graphdb.RelationshipType) entity).name());
            }
        });
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
