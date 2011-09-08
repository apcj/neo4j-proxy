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
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.proxy.eventmodel.EntityFinder;

public class PlaybackState implements EntityFinder {
    private GraphDatabaseService graphDatabase;
    private EntityCache.NodeCache nodeCache = new EntityCache.NodeCache();
    private EntityCache.RelationshipCache relationshipCache = new EntityCache.RelationshipCache();
    private Transaction currentTransaction = null;

    public PlaybackState(GraphDatabaseService graphDatabase) {
        this.graphDatabase = graphDatabase;
    }

    public void capture(Object result) {
        if (result instanceof Node) {
            nodeCache.put((Node) result);
        }
        if (result instanceof Relationship) {
            relationshipCache.put((Relationship) result);
        }
        if (result instanceof Transaction) {
            currentTransaction = (Transaction) result;
        }
    }

    public GraphDatabaseService getGraphDatabase() {
        return graphDatabase;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public Node getNode(long id) {
        return nodeCache.get(id);
    }

    public Relationship getRelationship(long id) {
        return relationshipCache.get(id);
    }
}
