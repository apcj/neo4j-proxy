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

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.proxy.playback.PlaybackState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Parameter {

    private static Pattern pattern = Pattern.compile("(.+)\\((.*)\\)");

    public static Parameter parse(String token) {
        Matcher matcher = pattern.matcher(token);
        matcher.find();
        String type = matcher.group(1);
        String value = matcher.group(2);
        if (org.neo4j.proxy.eventmodel.RelationshipType.accepts(type)) {
            return org.neo4j.proxy.eventmodel.RelationshipType.parse(type, value);
        } else if (PrimitiveValue.accepts(type)) {
            return PrimitiveValue.parse(type, value);
        } else if (GraphEntity.accepts(type)) {
            return GraphEntity.parse(type, value);
        }
        throw new IllegalArgumentException("Cannot parse: " + token);
    }

    public static Parameter fromObject(Object argument) {
        if (argument instanceof RelationshipType) {
            return org.neo4j.proxy.eventmodel.RelationshipType.fromObject(argument);
        } else if (argument instanceof Node) {
            return GraphEntity.fromObject(argument);
        } else {
            return PrimitiveValue.fromObject(argument);
        }
    }

    public abstract Object getValue(PlaybackState playbackState);
}
