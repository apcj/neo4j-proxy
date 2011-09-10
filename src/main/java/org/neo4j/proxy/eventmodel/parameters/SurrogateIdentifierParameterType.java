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

class SurrogateIdentifierParameterType extends BaseParameterType {

    private int idSequence = 0;

    public SurrogateIdentifierParameterType(Class wrappedType) {
        super(wrappedType);
    }

    class LongIdentifiedParameter implements Parameter {
        private int id;

        public LongIdentifiedParameter(int id) {
            this.id = id;
        }

        public ParameterType getType() {
            return SurrogateIdentifierParameterType.this;
        }

        public Object getValueForPlayback(EntityFinder entityFinder) {
            return entityFinder.findBySurrogateIdentifier(getWrappedType(), id);
        }

        public Object getValueForSerialization() {
            return id;
        }
    }

    public Class getSerializedType() {
        return int.class;
    }

    public Parameter fromSerializedValue(String typeString, Object serializedValue) {
        return new LongIdentifiedParameter((Integer) serializedValue);
    }

    public Parameter fromObject(Object entity) {
        return new LongIdentifiedParameter(idSequence++);
    }
}
