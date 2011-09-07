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

import org.neo4j.graphdb.*;
import org.neo4j.proxy.eventmodel.parameters.BaseParameter;
import org.neo4j.proxy.eventmodel.parameters.BaseParameterType;
import org.neo4j.proxy.playback.PlaybackState;

import java.util.ArrayList;
import java.util.List;

public class ParameterFactory {

    public static final List<ParameterType> types = new ArrayList<ParameterType>();
    static {
        types.add(new BaseParameterType(GraphDatabaseService.class) {
            public Class getSerializedType() {
                return String.class;
            }

            class GraphDatabaseServiceParameter extends BaseParameter {
                protected GraphDatabaseServiceParameter(ParameterType type) {
                    super(type);
                }

                public Object getValueForPlayback(PlaybackState playbackState) {
                    return playbackState.getGraphDatabase();
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

                public Object getValueForPlayback(PlaybackState playbackState) {
                    return playbackState.getCurrentTransaction();
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

                public Object getValueForPlayback(PlaybackState playbackState) {
                    return playbackState.getNodeCache().get(id);
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

                public Object getValueForPlayback(PlaybackState playbackState) {
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
        types.add(new BaseParameterType(Direction.class) {

            public Class getSerializedType() {
                return String.class;
            }

            class DirectionParameter extends BaseParameter {
                private org.neo4j.graphdb.Direction direction;

                DirectionParameter(ParameterType type, Direction direction) {
                    super(type);
                    this.direction = direction;
                }

                public Object getValueForPlayback(PlaybackState playbackState) {
                    return direction;
                }

                public Object getValueForSerialization() {
                    return direction.name();
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new DirectionParameter(this, org.neo4j.graphdb.Direction.valueOf((String) serializedValue));
            }

            public Parameter fromObject(Object entity) {
                return new DirectionParameter(this, (org.neo4j.graphdb.Direction) entity);
            }
        });
        types.add(new BaseParameterType(String.class) {

            class StringParameter extends BaseParameter {
                private String value;

                public StringParameter(ParameterType type, String value) {
                    super(type);
                    this.value = value;
                }

                public Object getValueForPlayback(PlaybackState playbackState) {
                    return value;
                }

                public Object getValueForSerialization() {
                    return value;
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new StringParameter(this, (String) serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new StringParameter(this, (String) entity);
            }
        });
        types.add(new BaseParameterType(Integer.class) {

            class IntegerParameter extends BaseParameter {
                private int value;

                IntegerParameter(ParameterType type, int value) {
                    super(type);
                    this.value = value;
                }

                public Object getValueForPlayback(PlaybackState playbackState) {
                    return value;
                }

                public Object getValueForSerialization() {
                    return value;
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new IntegerParameter(this, (Integer) serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new IntegerParameter(this, (Integer) entity);
            }
        });
        types.add(new BaseParameterType(int[].class) {
            class ArrayParameter extends BaseParameter {
                private int[] array;

                ArrayParameter(ParameterType type, int[] array) {
                    super(type);
                    this.array = array;
                }

                public Object getValueForPlayback(PlaybackState playbackState) {
                    return array;
                }

                public Object getValueForSerialization() {
                    return array;
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new ArrayParameter(this, (int[]) serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new ArrayParameter(this, (int[]) entity);
            }
        });
        types.add(new BaseParameterType(boolean[].class) {
            class ArrayParameter extends BaseParameter {
                private boolean[] array;

                ArrayParameter(ParameterType type, boolean[] array) {
                    super(type);
                    this.array = array;
                }

                public Object getValueForPlayback(PlaybackState playbackState) {
                    return array;
                }

                public Object getValueForSerialization() {
                    return array;
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new ArrayParameter(this, (boolean[]) serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new ArrayParameter(this, (boolean[]) entity);
            }
        });
        types.add(new BaseParameterType(Integer[].class) {

            class ArrayParameter extends BaseParameter {
                private Integer[] array;

                ArrayParameter(ParameterType type, Integer[] array) {
                    super(type);
                    this.array = array;
                }

                public Object getValueForPlayback(PlaybackState playbackState) {
                    return array;
                }

                public Object getValueForSerialization() {
                    return array;
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new ArrayParameter(this, (Integer[]) serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new ArrayParameter(this, (Integer[]) entity);
            }
        });
        types.add(new BaseParameterType(Boolean[].class) {

            class ArrayParameter extends BaseParameter {
                private Boolean[] array;

                ArrayParameter(ParameterType type, Boolean[] array) {
                    super(type);
                    this.array = array;
                }

                public Object getValueForPlayback(PlaybackState playbackState) {
                    return array;
                }

                public Object getValueForSerialization() {
                    return array;
                }
            }
            public Parameter fromSerializedValue(String typeString, Object serializedValue) {
                return new ArrayParameter(this, (Boolean[]) serializedValue);
            }

            public Parameter fromObject(Object entity) {
                return new ArrayParameter(this, (Boolean[]) entity);
            }
        });
    }
    public static Parameter fromObject(Object argument) {
        for (ParameterType type : types) {
            if (type.acceptObject(argument)) {
                return type.fromObject(argument);
            }
        }
        throw new IllegalArgumentException("Cannot accept type of argument: " + argument.getClass());
    }
}
