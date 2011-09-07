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

import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.sun.tools.internal.ws.processor.model.java.JavaArrayType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.proxy.playback.PlaybackState;
import sun.text.normalizer.IntTrie;

import javax.tools.JavaCompiler;
import java.util.Arrays;

public interface ParameterType {
    boolean acceptTypeName(String typeString);
    boolean acceptObject(Object object);
    Parameter fromStrings(String typeString, String valueString);
    public Parameter fromObject(Object entity);

    public enum Types implements ParameterType {
        GraphDatabaseService {
            public boolean acceptTypeName(String typeString) {
                return "GraphDatabaseService".equals(typeString);
            }

            public boolean acceptObject(Object object) {
                return object instanceof GraphDatabaseService;
            }

            class GraphDatabaseServiceParameter implements Parameter {
                public Object getValue(PlaybackState playbackState) {
                    return playbackState.getGraphDatabase();
                }

                public Class apiClass() {
                    return GraphDatabaseService.class;
                }

                public String valueAsString() {
                    return "";
                }
            }

            public Parameter fromStrings(String typeString, String valueString) {
                return new GraphDatabaseServiceParameter();
            }

            public Parameter fromObject(Object entity) {
                return new GraphDatabaseServiceParameter();
            }
        },
        Transaction {
            public boolean acceptTypeName(String typeString) {
                return "Transaction".equals(typeString);
            }

            public boolean acceptObject(Object object) {
                return object instanceof Transaction;
            }

            class TransactionParameter implements Parameter {
                public Object getValue(PlaybackState playbackState) {
                    return playbackState.getCurrentTransaction();
                }

                public Class apiClass() {
                    return Transaction.class;
                }

                public String valueAsString() {
                    return "";
                }
            }
            public Parameter fromStrings(String typeString, String valueString) {
                return new TransactionParameter();
            }

            public Parameter fromObject(Object entity) {
                return new TransactionParameter();
            }
        },
        Node {
            public boolean acceptTypeName(String typeString) {
                return "Node".equals(typeString);
            }

            public boolean acceptObject(Object object) {
                return object instanceof Node;
            }

            class NodeParameter implements Parameter {
                private long id;

                public NodeParameter(long id) {
                    this.id = id;
                }

                public Object getValue(PlaybackState playbackState) {
                    return playbackState.getNodeCache().get(id);
                }

                public Class apiClass() {
                    return Node.class;
                }

                public String valueAsString() {
                    return java.lang.String.valueOf(id);
                }
            }
            public Parameter fromStrings(String typeString, String valueString) {
                return new NodeParameter(Long.parseLong(valueString));
            }

            public Parameter fromObject(Object entity) {
                return new NodeParameter(((Node) entity).getId());
            }
        },
        RelationshipType {
            public boolean acceptTypeName(String typeString) {
                return "RelationshipType".equals(typeString);
            }
            public boolean acceptObject(Object object) {
                return object instanceof org.neo4j.graphdb.RelationshipType;
            }

            class RelationshipTypeParameter implements Parameter, org.neo4j.graphdb.RelationshipType {
                private String name;

                RelationshipTypeParameter(String name) {
                    this.name = name;
                }

                public Object getValue(PlaybackState playbackState) {
                    return this;
                }

                public String name() {
                    return name;
                }

                public Class apiClass() {
                    return RelationshipType.class;
                }

                public String valueAsString() {
                    return "\"" + name + "\"";
                }
            }
            public Parameter fromStrings(String typeString, String valueString) {
                return new RelationshipTypeParameter(valueString.substring(1, valueString.length() - 1));
            }

            public Parameter fromObject(Object entity) {
                return new RelationshipTypeParameter(((org.neo4j.graphdb.RelationshipType) entity).name());
            }
        },
        Direction {
            public boolean acceptTypeName(String typeString) {
                return "Direction".equals(typeString);
            }
            public boolean acceptObject(Object object) {
                return object instanceof org.neo4j.graphdb.Direction;
            }

            class DirectionParameter implements Parameter {
                private org.neo4j.graphdb.Direction direction;

                DirectionParameter(org.neo4j.graphdb.Direction direction) {
                    this.direction = direction;
                }

                public Object getValue(PlaybackState playbackState) {
                    return direction;
                }

                public Class apiClass() {
                    return org.neo4j.graphdb.Direction.class;
                }

                public String valueAsString() {
                    return direction.name();
                }
            }
            public Parameter fromStrings(String typeString, String valueString) {
                return new DirectionParameter(org.neo4j.graphdb.Direction.valueOf(valueString));
            }

            public Parameter fromObject(Object entity) {
                return new DirectionParameter((org.neo4j.graphdb.Direction) entity);
            }
        },
        String {
            public boolean acceptTypeName(String typeString) {
                return "String".equals(typeString);
            }

            public boolean acceptObject(Object object) {
                return object instanceof String;
            }

            class StringParameter implements Parameter {
                private String value;

                public StringParameter(String value) {
                    this.value = value;
                }

                public Object getValue(PlaybackState playbackState) {
                    return value;
                }

                public Class apiClass() {
                    return String.class;
                }

                public String valueAsString() {
                    return "\"" + value + "\"";
                }
            }
            public Parameter fromStrings(String typeString, String valueString) {
                return new StringParameter(valueString.substring(1, valueString.length() - 1));
            }

            public Parameter fromObject(Object entity) {
                return new StringParameter((String) entity);
            }
        },
        Integer {
            public boolean acceptTypeName(String typeString) {
                return "Integer".equals(typeString);
            }

            public boolean acceptObject(Object object) {
                return object instanceof Integer;
            }

            class IntegerParameter implements Parameter {
                private int value;

                IntegerParameter(int value) {
                    this.value = value;
                }

                public Object getValue(PlaybackState playbackState) {
                    return value;
                }

                public Class apiClass() {
                    return Integer.class;
                }

                public String valueAsString() {
                    return java.lang.String.valueOf(value);
                }
            }
            public Parameter fromStrings(String typeString, String valueString) {
                return new IntegerParameter(java.lang.Integer.parseInt(valueString));
            }

            public Parameter fromObject(Object entity) {
                return new IntegerParameter((Integer) entity);
            }
        },
        Array {
            public boolean acceptTypeName(String typeString) {
                return typeString.endsWith("[]");
            }

            public boolean acceptObject(Object object) {
                return object instanceof int[];
            }

            class ArrayParameter implements Parameter {
                private int[] array;

                ArrayParameter(int[] array) {
                    this.array = array;
                }

                public Object getValue(PlaybackState playbackState) {
                    return array;
                }

                public Class apiClass() {
                    return int[].class;
                }

                public String valueAsString() {
                    StringBuilder builder = new StringBuilder("{");
                    for (int i = 0; i < array.length; i++) {
                        builder.append(array[i]);
                        if (i < array.length - 1) {
                            builder.append(", ");
                        }
                    }
                    return builder.append("}").toString();
                }
            }
            public Parameter fromStrings(String typeString, String valueString) {
                String[] tokens = new String[0];
                if (valueString.contains(", ")) {
                    tokens = valueString.substring(1, valueString.length() - 1).split(", ");
                }
                int[] array = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    array[i] = java.lang.Integer.parseInt(tokens[i]);
                }
                return new ArrayParameter(array);
            }

            public Parameter fromObject(Object entity) {
                return new ArrayParameter((int[]) entity);
            }
        }
    }
}
