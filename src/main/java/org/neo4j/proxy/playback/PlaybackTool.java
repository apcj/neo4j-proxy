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
package org.neo4j.proxy.playback;

import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.eventmodel.serialization.JacksonDeserializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class PlaybackTool {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 2) {
            new PlaybackTool().run(args[0], args[1]);
        } else {
            System.out.println("Usage: java " + PlaybackTool.class + " <database-store-directory> <event-log-file>");
        }
    }

    private void run(String storeDirectory, String eventLog) throws FileNotFoundException {
        EmbeddedGraphDatabase database = new EmbeddedGraphDatabase(storeDirectory);
        JacksonDeserializer events = new JacksonDeserializer(new BufferedReader(new FileReader(eventLog)));

        new PlaybackDriver(database).playback(events, new ConsoleEventLogger());
    }

    private class ConsoleEventLogger extends PlaybackDriver.HaltOnException {
        @Override
        public void beforePlayback(Event event) {
            System.out.println("Playing back: " + event);
        }
    }
}
