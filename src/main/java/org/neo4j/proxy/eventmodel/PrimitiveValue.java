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

import org.neo4j.proxy.playback.NodeCache;

import java.util.ArrayList;
import java.util.List;

public class PrimitiveValue extends Parameter {
    public enum SupportedTypes {
        Integer {
            public String format(Object value) {
                return java.lang.String.valueOf(value);
            }
            public Object parse(String valueString) {
                return java.lang.Integer.parseInt(valueString);
            }
        }, String {
            public String format(Object value) {
                return "\"" + value + "\"";
            }
            public Object parse(String valueString) {
                return valueString.substring(1, valueString.length() - 1);
            }
        };

        public abstract Object parse(String valueString);
        public abstract String format(Object value);
    }

    private static List<String> typeKeys = new ArrayList<String>();
    static {
        for (SupportedTypes type : SupportedTypes.values()) {
            typeKeys.add(type.name());
        }
    }
    private SupportedTypes type;
    private Object value;

    public PrimitiveValue(SupportedTypes type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public SupportedTypes getType() {
        return type;
    }

    public String toString() {
        return type + "(" + type.format(value) + ")";
    }

    public static boolean accepts(String type) {
        return typeKeys.contains(type);
    }

    public static PrimitiveValue parse(String typeString, String valueString) {
        SupportedTypes type = SupportedTypes.valueOf(typeString);
        return new PrimitiveValue(type, type.parse(valueString));
    }

    public static PrimitiveValue fromObject(Object object) {
        return new PrimitiveValue(SupportedTypes.valueOf(object.getClass().getSimpleName()), object);
    }

    public Object getValue(NodeCache nodeCache) {
        return getValue();
    }
}
