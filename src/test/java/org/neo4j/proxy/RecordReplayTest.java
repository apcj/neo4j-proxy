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
package org.neo4j.proxy;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.playback.PlaybackDriver;
import org.neo4j.proxy.recording.RecordingGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RecordReplayTest {

    @Test
    public void shouldRecordAndPlayback()
    {
        String recordedStoreDir = "target/recordedDatabase";
        final List<Event> events = writeToDatabase(recordedStoreDir);
        makeAssertionsAboutTheData(recordedStoreDir);

        String playbackStoreDir = "target/playbackDatabase";
        playbackToDifferentDatabase(events, playbackStoreDir);
        makeAssertionsAboutTheData(playbackStoreDir);
    }

    private void makeAssertionsAboutTheData(String playbackStoreDir) {
        EmbeddedReadOnlyGraphDatabase readDatabase = new EmbeddedReadOnlyGraphDatabase(playbackStoreDir);
        Iterable<Relationship> relationships = readDatabase.getNodeById(1).getRelationships(RelationshipTypes.working_on, Direction.INCOMING);
        int relationshipCount = 0;
        for (Relationship relationship : relationships) {
            relationshipCount++;
        }
        assertEquals(3, relationshipCount);
        readDatabase.shutdown();
    }

    private EmbeddedGraphDatabase playbackToDifferentDatabase(List<Event> events, String playbackStoreDir) {
        clean(playbackStoreDir);

        EmbeddedGraphDatabase playbackGraphDatabase = new EmbeddedGraphDatabase(playbackStoreDir);
        new PlaybackDriver(playbackGraphDatabase).playback(events);

        playbackGraphDatabase.shutdown();
        return playbackGraphDatabase;
    }

    private List<Event> writeToDatabase(String storeDir) {
        clean(storeDir);

        final List<Event> events = new ArrayList<Event>();

        GraphDatabaseService recordingGraphDatabase = RecordingGraphDatabase.create(new RecordingGraphDatabase.Listener() {
            public void onEvent(Event event) {
                events.add(event);
            }
        }, new EmbeddedGraphDatabase(storeDir));

        addData(recordingGraphDatabase);

        recordingGraphDatabase.shutdown();
        return events;
    }

    private void clean(String storeDir) {
        try {
            FileUtils.deleteDirectory(new File(storeDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addData(GraphDatabaseService graphDatabase) {
        Transaction tx = graphDatabase.beginTx();
        try {
            Node storeRefactoring = graphDatabase.createNode();

            Node alistair = graphDatabase.createNode();
            alistair.setProperty("name", "Alistair");
            Node chris = graphDatabase.createNode();
            chris.setProperty("name", "Chris");
            Node mattias = graphDatabase.createNode();
            mattias.setProperty("name", "Mattias");

            alistair.createRelationshipTo(storeRefactoring, RelationshipTypes.working_on);
            chris.createRelationshipTo(storeRefactoring, RelationshipTypes.working_on);
            mattias.createRelationshipTo(storeRefactoring, RelationshipTypes.working_on);
            tx.success();
        } finally {
            tx.finish();
        }
    }

    public enum RelationshipTypes implements RelationshipType {
        working_on
    }
}
