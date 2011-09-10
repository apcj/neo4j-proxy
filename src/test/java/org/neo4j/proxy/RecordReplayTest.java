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
import org.neo4j.proxy.eventmodel.serialization.JacksonDeserializer;
import org.neo4j.proxy.eventmodel.serialization.JacksonSerializer;
import org.neo4j.proxy.playback.PlaybackDriver;
import org.neo4j.proxy.recording.RecordingGraphDatabase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RecordReplayTest {

    @Test
    public void shouldRecordAndPlayback()
    {
        String recordedStoreDir = "target/recordedDatabase";
        final List<Event> events = new ArrayList<Event>();
        writeToDatabase(recordedStoreDir, new EventListAccumulator(events));
        makeAssertionsAboutTheData(recordedStoreDir);

        String playbackStoreDir = "target/playbackDatabase";
        playbackToDifferentDatabase(events, playbackStoreDir);
        makeAssertionsAboutTheData(playbackStoreDir);
    }

    @Test
    public void shouldRecordAndPlaybackAfterSerialisingEvents()
    {
        String recordedStoreDir = "target/recordedDatabase";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JacksonSerializer serializer = new JacksonSerializer(new PrintWriter(byteArrayOutputStream));
        writeToDatabase(recordedStoreDir, serializer);
        serializer.flush();
        makeAssertionsAboutTheData(recordedStoreDir);

        String playbackStoreDir = "target/playbackDatabase";
        playbackToDifferentDatabase(new JacksonDeserializer(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())))), playbackStoreDir);
        makeAssertionsAboutTheData(playbackStoreDir);
    }

    @Test
    public void printEventsToConsole()
    {
        String recordedStoreDir = "target/recordedDatabase";
        JacksonSerializer serializer = new JacksonSerializer(new PrintWriter(System.out));
        writeToDatabase(recordedStoreDir, serializer);
        serializer.flush();
    }

    private EmbeddedGraphDatabase playbackToDifferentDatabase(Iterable<Event> events, String playbackStoreDir) {
        clean(playbackStoreDir);

        EmbeddedGraphDatabase playbackGraphDatabase = new EmbeddedGraphDatabase(playbackStoreDir);
        new PlaybackDriver(playbackGraphDatabase).playback(events);

        playbackGraphDatabase.shutdown();
        return playbackGraphDatabase;
    }

    private void writeToDatabase(String storeDir, Event.Listener listener) {
        clean(storeDir);

        GraphDatabaseService recordingGraphDatabase = RecordingGraphDatabase.create(listener, new EmbeddedGraphDatabase(storeDir));

        addData(recordingGraphDatabase);

        recordingGraphDatabase.shutdown();
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

            Relationship relationship1 = alistair.createRelationshipTo(storeRefactoring, RelationshipTypes.working_on);
            relationship1.setProperty("effort", 1d);
            Relationship relationship2 = chris.createRelationshipTo(storeRefactoring, RelationshipTypes.working_on);
            relationship2.setProperty("effort", 1d);
            Relationship relationship3 = mattias.createRelationshipTo(storeRefactoring, RelationshipTypes.working_on);
            relationship3.setProperty("effort", 0.5d);

            Iterable<Relationship> relationships = storeRefactoring.getRelationships(RelationshipTypes.working_on, Direction.INCOMING);
            for (Relationship relationship : relationships) {
                Node developer = relationship.getStartNode();
                developer.setProperty("has_worked_on_store_refactoring", true);
            }

            tx.success();
        } finally {
            tx.finish();
        }
    }

    private void makeAssertionsAboutTheData(String playbackStoreDir) {
        EmbeddedReadOnlyGraphDatabase readDatabase = new EmbeddedReadOnlyGraphDatabase(playbackStoreDir);
        Iterable<Relationship> relationships = readDatabase.getNodeById(1).getRelationships(RelationshipTypes.working_on, Direction.INCOMING);
        int relationshipCount = 0;
        for (Relationship relationship : relationships) {
            relationshipCount++;
            Node developer = relationship.getStartNode();
            String name = (String) developer.getProperty("name");
            if (name.equals("Alistair") || name.equals("Chris")) {
                assertEquals(1.0d, (Double) relationship.getProperty("effort"), 0.01d);
            } else {
                assertEquals(0.5d, (Double) relationship.getProperty("effort"), 0.01d);
            }
            assertTrue((Boolean) developer.getProperty("has_worked_on_store_refactoring"));
        }
        assertEquals(3, relationshipCount);
        readDatabase.shutdown();
    }

    public enum RelationshipTypes implements RelationshipType {
        working_on
    }

}
