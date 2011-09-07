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

import org.neo4j.proxy.eventmodel.Parameter;
import org.neo4j.proxy.eventmodel.ParameterType;

public abstract class BaseParameter implements Parameter {

    private ParameterType type;

    protected BaseParameter(ParameterType type) {
        this.type = type;
    }

    public ParameterType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseParameter)) return false;

        BaseParameter that = (BaseParameter) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (valueAsString() != null ? !valueAsString().equals(that.valueAsString()) : that.valueAsString() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
