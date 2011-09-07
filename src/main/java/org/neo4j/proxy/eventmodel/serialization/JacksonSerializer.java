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
import org.neo4j.proxy.eventmodel.Parameter;
import org.neo4j.proxy.eventmodel.ParameterFactory;
import org.neo4j.proxy.eventmodel.ParameterType;
import org.neo4j.proxy.recording.RecordingGraphDatabase;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JacksonSerializer implements RecordingGraphDatabase.Listener {

    private static ObjectMapper mapper = new ObjectMapper();

    private PrintWriter writer;

    public JacksonSerializer(PrintWriter writer) {
        this.writer = writer;
    }

    public void onEvent(Event event) {
        writer.println(serializeEvent(event).toString());
    }

    public void flush() {
        writer.flush();
    }

    public static JsonNode serializeEvent(Event event) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("target", serializeParameter(event.getTarget()));
        node.put("methodName", event.getMethodName());
        node.put("arguments", serializeArguments(event.getParameters()));
        return node;
    }

    public static Event parseEvent(JsonNode node) {
        return new Event(
                parseParameter(node.get("target")),
                node.get("methodName").getTextValue(),
                parseArguments(node.get("arguments")));
    }

    private static JsonNode serializeArguments(Parameter[] arguments) {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for (Parameter argument : arguments) {
            arrayNode.add(serializeParameter(argument));
        }
        return arrayNode;
    }

    private static Parameter[] parseArguments(JsonNode arguments) {
        Iterator<JsonNode> elements = arguments.getElements();
        List<Parameter> parameters = new ArrayList<Parameter>();
        while (elements.hasNext()) {
            parameters.add(parseParameter(elements.next()));
        }
        return parameters.toArray(new Parameter[parameters.size()]);
    }

    private static JsonNode serializeParameter(Parameter argument) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("type", argument.getType().getWrappedType().getSimpleName());
        node.put("value", argument.valueAsString());
        return node;
    }

    private static Parameter parseParameter(JsonNode jsonNode) {
        String typeName = jsonNode.get("type").getTextValue();
        String value = jsonNode.get("value").getTextValue();
        for (ParameterType type : ParameterFactory.types) {
            if (type.acceptTypeName(typeName)) {
                return type.fromStrings(typeName, value);
            }
        }
        throw new IllegalArgumentException("Cannot parse parameter: " + jsonNode);
    }
}
