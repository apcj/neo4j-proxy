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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeCache {

    private static Pattern pattern  = Pattern.compile("\\[([0-9]+)\\]");
    private Map<Long, Node> cache = new HashMap<Long, Node>();

    public Node get(long id) {
        return cache.get(id);
    }

    public static long nodeId(String eventTarget) {
        Matcher matcher = pattern.matcher(eventTarget);
        matcher.find();
        String group = matcher.group(1);
        return Long.parseLong(group);
    }

    public void put(Node node) {
        cache.put(node.getId(), node);
    }
}
