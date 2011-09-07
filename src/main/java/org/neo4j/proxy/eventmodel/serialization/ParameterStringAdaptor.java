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
package org.neo4j.proxy.eventmodel.serialization;

import org.neo4j.proxy.eventmodel.Parameter;
import org.neo4j.proxy.eventmodel.ParameterFactory;
import org.neo4j.proxy.eventmodel.ParameterType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterStringAdaptor {

    private static Pattern pattern = Pattern.compile("(.+)\\((.*)\\)");

    public static Parameter parse(String token) {
        Matcher matcher = pattern.matcher(token);
        matcher.find();
        String typeName = matcher.group(1);
        String value = matcher.group(2);
        for (ParameterType type : ParameterFactory.types) {
            if (type.acceptTypeName(typeName)) {
                return type.fromStrings(typeName, value);
            }
        }
        throw new IllegalArgumentException("Cannot parse parameter: " + token);
    }

    public static String serialize(Parameter parameter) {
        return String.format("%s(%s)", parameter.getType().getWrappedType().getSimpleName(), parameter.valueAsString());
    }
}
