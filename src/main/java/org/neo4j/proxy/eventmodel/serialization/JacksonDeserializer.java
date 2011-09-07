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

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.proxy.eventmodel.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

public class JacksonDeserializer implements Iterable<Event> {
    private BufferedReader reader;
    ObjectMapper mapper = new ObjectMapper();

    public JacksonDeserializer(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public Iterator<Event> iterator() {
        return new Iterator<Event>() {
            private String line;

            private void fillBuffer() {
                if (line == null) {
                    try {
                        line = reader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public boolean hasNext() {
                fillBuffer();
                return line != null;
            }

            @Override
            public Event next() {
                fillBuffer();
                if (line == null) {
                    throw new IllegalStateException("next() called at end of reader");
                }
                Event event = null;
                try {
                    event = JacksonSerializer.parseEvent(mapper.readTree(line));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                line = null;
                return event;
            }

            @Override
            public void remove() {
            }
        };
    }
}
