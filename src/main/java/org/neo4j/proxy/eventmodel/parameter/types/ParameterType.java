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
package org.neo4j.proxy.eventmodel.parameter.types;

import org.neo4j.proxy.eventmodel.EntityFinder;
import org.neo4j.proxy.eventmodel.parameter.Parameter;

public abstract class ParameterType {

    protected Class wrappedType;
    protected Class serializedType;

    public ParameterType(Class wrappedType, Class serializedType) {
        this.wrappedType = wrappedType;
        this.serializedType = serializedType;
    }

    public Class getWrappedType() {
        return wrappedType;
    }

    public Class getSerializedType() {
        return serializedType;
    }

    public boolean acceptTypeName(String typeString) {
        return wrappedType.getSimpleName().equals(typeString);
    }

    public boolean acceptObject(Object object) {
        return wrappedType.isAssignableFrom(object.getClass());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterType)) return false;

        ParameterType that = (ParameterType) o;

        if (wrappedType != null ? !wrappedType.equals(that.wrappedType) : that.wrappedType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return wrappedType != null ? wrappedType.hashCode() : 0;
    }

    public abstract Object getValueForPlayback(Object serializedValue, EntityFinder entityFinder);

    public abstract Parameter fromSerializedValue(String typeString, Object serializedValue);

    public abstract Parameter fromObject(Object entity);
}
