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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.proxy.eventmodel.Event;
import org.neo4j.proxy.recording.RecordingGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class RecordingTest {
    @Test
    public void shouldRecordDatabaseLevelEvents()
    {
        //given
        String storeDir = "target/databaseLevelEvents";
        clean(storeDir);
        ArrayList<Event> events = new ArrayList<Event>();
        GraphDatabaseService database = RecordingGraphDatabase.create(new EventListAccumulator(events), new EmbeddedGraphDatabase(storeDir));

        //when
        Transaction transaction = database.beginTx();
        transaction.success();
        transaction.finish();
        database.shutdown();

        //then
        assertEquals("beginTx", events.get(0).getMethodName());
        assertEquals("shutdown", events.get(3).getMethodName());
    }

    @Test
    public void shouldRecordNodeCreation()
    {
        //given
        String storeDir = "target/databaseLevelEvents";
        clean(storeDir);
        ArrayList<Event> events = new ArrayList<Event>();
        GraphDatabaseService database = RecordingGraphDatabase.create(new EventListAccumulator(events), new EmbeddedGraphDatabase(storeDir));

        //when
        Transaction transaction = database.beginTx();
        database.createNode();
        database.createNode();
        transaction.success();
        transaction.finish();
        database.shutdown();

        //then
        assertEquals("createNode", events.get(1).getMethodName());
        assertEquals("Node", events.get(1).getResult().getType().getWrappedType().getSimpleName());
        assertEquals(1L, events.get(1).getResult().getValueForSerialization());
        assertEquals("createNode", events.get(2).getMethodName());
        assertEquals("Node", events.get(2).getResult().getType().getWrappedType().getSimpleName());
        assertEquals(2L, events.get(2).getResult().getValueForSerialization());
    }

    private void clean(String storeDir) {
        try {
            FileUtils.deleteDirectory(new File(storeDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
