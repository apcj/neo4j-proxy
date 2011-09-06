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

import org.neo4j.proxy.playback.PlaybackState;

public class RelationshipType extends Parameter implements org.neo4j.graphdb.RelationshipType {
    private String name;

    public RelationshipType(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public static boolean accepts(String type) {
        return RelationshipType.class.getSimpleName().equals(type);
    }

    public static RelationshipType parse(String typeString, String valueString) {
        return new RelationshipType(valueString.substring(1, valueString.length() - 1));
    }

    public static RelationshipType fromObject(Object argument) {
        return new RelationshipType(((org.neo4j.graphdb.RelationshipType) argument).name());
    }

    public Object getValue(PlaybackState playbackState) {
        return this;
    }

    @Override
    public String toString() {
        return String.format("RelationshipType(\"%s\")", name);
    }
}
