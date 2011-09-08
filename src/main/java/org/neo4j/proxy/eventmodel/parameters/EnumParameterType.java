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
package org.neo4j.proxy.eventmodel.parameters;

import org.neo4j.proxy.eventmodel.EntityFinder;

public class EnumParameterType extends BaseParameterType {

    public EnumParameterType(Class<?> wrappedType) {
        super(wrappedType);
    }

    public Class getSerializedType() {
        return String.class;
    }

    class EnumParameter implements Parameter {
        private Object value;

        EnumParameter(Object value) {
            this.value = value;
        }

        public ParameterType getType() {
            return EnumParameterType.this;
        }

        public Object getValueForPlayback(EntityFinder entityFinder) {
            return value;
        }

        public Object getValueForSerialization() {
            return ((Enum) value).name();
        }
    }

    public Parameter fromSerializedValue(String typeString, Object serializedValue) {
        return new EnumParameter(Enum.valueOf(wrappedType, (String) serializedValue));
    }

    public Parameter fromObject(Object entity) {
        return new EnumParameter(entity);
    }
}