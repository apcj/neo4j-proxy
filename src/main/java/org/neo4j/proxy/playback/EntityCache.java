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

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.w3c.dom.Entity;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class EntityCache<T> {

    protected Map<Long, T> cache = new HashMap<Long, T>();

    public T get(long id) {
        return cache.get(id);
    }

    public abstract void put(T node);

    public static class NodeCache extends EntityCache<Node> {

        public void put(Node node) {
            cache.put(node.getId(), node);
        }
    }

    public static class RelationshipCache extends EntityCache<Relationship> {

        public void put(Relationship node) {
            cache.put(node.getId(), node);
        }
    }
}
