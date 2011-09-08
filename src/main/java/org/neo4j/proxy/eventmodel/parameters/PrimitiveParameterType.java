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

public class PrimitiveParameterType extends BaseParameterType {
    public PrimitiveParameterType(Class primitiveType) {
        super(primitiveType);
    }

    class PrimitiveParameter implements Parameter {

        private Object value;

        PrimitiveParameter(Object value) {
            this.value = value;
        }

        public ParameterType getType() {
            return PrimitiveParameterType.this;
        }

        public Object getValueForPlayback(EntityFinder entityFinder) {
            return value;
        }

        public Object getValueForSerialization() {
            return value;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PrimitiveParameter that = (PrimitiveParameter) o;

            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

    public Parameter fromSerializedValue(String typeString, Object serializedValue) {
        return new PrimitiveParameter(serializedValue);
    }

    public Parameter fromObject(Object entity) {
        return new PrimitiveParameter(entity);
    }
}