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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.proxy.playback.PlaybackState;

import java.util.ArrayList;
import java.util.List;

public class GraphEntity extends Parameter {
    private Kinds kind;
    private long id;

    private static List<String> kindKeys = new ArrayList<String>();

    public GraphEntity(Kinds kind, long id) {
        this.kind = kind;
        this.id = id;
    }

    static {
        for (Kinds kind : Kinds.values()) {
            kindKeys.add(kind.name());
        }
    }

    public Kinds getKind() {
        return kind;
    }

    public long getId() {
        return id;
    }

    public static boolean accepts(String type) {
        return kindKeys.contains(type);
    }

    public enum Kinds {
        GraphDatabaseService {
            public Class apiClass() {
                return GraphDatabaseService.class;
            }
            Object lookupValueFromPlaybackState(long id, PlaybackState playbackState) {
                return playbackState.getGraphDatabase();
            }
        }, Node {
            public Class apiClass() {
                return Node.class;
            }
            Object lookupValueFromPlaybackState(long id, PlaybackState playbackState) {
                return playbackState.getNodeCache().get(id);
            }
        }, Transaction {
            public Class apiClass() {
                return Transaction.class;
            }
            Object lookupValueFromPlaybackState(long id, PlaybackState playbackState) {
                return playbackState.getCurrentTransaction();
            }
        };

        public abstract Class apiClass();
        abstract Object lookupValueFromPlaybackState(long id, PlaybackState playbackState);
    }

    public static GraphEntity parse(String typeString, String valueString) {
        Kinds type = Kinds.valueOf(typeString);
        return new GraphEntity(type, Long.parseLong(valueString));
    }

    public static GraphEntity fromObject(Object entity) {
        if (entity instanceof GraphDatabaseService) {
            return new GraphEntity(Kinds.GraphDatabaseService, 0);
        } else if (entity instanceof Transaction) {
            return new GraphEntity(Kinds.Transaction, 0);
        } else if (entity instanceof Node) {
            return new GraphEntity(Kinds.Node, ((Node) entity).getId());
        }
        throw new IllegalArgumentException("Not a recognised GraphEntity type: " + entity);
    }

    public Object getValue(PlaybackState playbackState) {
        return kind.lookupValueFromPlaybackState(id, playbackState);
    }

    @Override
    public String toString() {
        return String.format("%s(%d)", kind, id);
    }
}
