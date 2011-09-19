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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.eventmodel.parameter.Parameter;
import org.neo4j.proxy.eventmodel.parameter.ParameterFactory;
import org.neo4j.proxy.eventmodel.parameter.types.ParameterType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JacksonAdaptor {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode serializeEvent(Event event) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("target", serializeParameter(event.getTarget()));
        node.put("method", event.getMethodName());
        node.put("args", serializeArguments(event.getParameters()));
        node.put("result", serializeParameter(event.getResult()));
        return node;
    }

    public static Event parseEvent(JsonNode node) {
        return new Event(
                parseParameter(node.get("target")),
                node.get("method").getTextValue(),
                parseArguments(node.get("args")),
                parseParameter(node.get("result")));
    }

    public static JsonNode serializeArguments(Parameter[] arguments) {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Parameter argument : arguments) {
            arrayNode.add(serializeParameter(argument));
        }
        return arrayNode;
    }

    public static Parameter[] parseArguments(JsonNode arguments) {
        Iterator<JsonNode> elements = arguments.getElements();
        List<Parameter> parameters = new ArrayList<Parameter>();
        while (elements.hasNext()) {
            parameters.add(parseParameter(elements.next()));
        }
        return parameters.toArray(new Parameter[parameters.size()]);
    }

    public static JsonNode serializeParameter(Parameter argument) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();

        String type = argument.getType().getWrappedType().getSimpleName();

        Object value = argument.getValueForSerialization();
        value = workAroundProblemWithSerializationOfPrimitiveByteArrays(value);

        node.put(type, mapper.<JsonNode>valueToTree(value));

        return node;
    }

    private static Object workAroundProblemWithSerializationOfPrimitiveByteArrays(Object valueForSerialization) {
        if (valueForSerialization instanceof byte[]) {
            byte[] originalValue = (byte[]) valueForSerialization;
            Byte[] retypedValue = new Byte[originalValue.length];
            for (int i = 0; i < originalValue.length; i++) {
                retypedValue[i] = originalValue[i];
            }
            valueForSerialization = retypedValue;
        }
        return valueForSerialization;
    }

    private static final ParameterFactory parameterFactory = new ParameterFactory();

    public static Parameter parseParameter(JsonNode jsonNode) {
        Iterator<Map.Entry<String, JsonNode>> fields = ((ObjectNode) jsonNode).getFields();
        Map.Entry<String, JsonNode> entry = fields.next();
        if (fields.hasNext()) {
            throw new IllegalArgumentException("Parameter should only have one field");
        }
        String typeName = entry.getKey();
        for (ParameterType type : parameterFactory.types) {
            if (type.acceptTypeName(typeName)) {
                try {
                    Object serializedValue = mapper.<Object>treeToValue(entry.getValue(), type.getSerializedType());
                    return type.fromSerializedValue(typeName, serializedValue);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new IllegalArgumentException("Cannot parse parameter: " + jsonNode);
    }
}
