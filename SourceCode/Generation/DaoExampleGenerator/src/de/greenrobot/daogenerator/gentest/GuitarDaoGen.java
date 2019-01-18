
/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.daogenerator.gentest;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Generates entities and DAOs for the example project DaoExample.
 * 
 * Run it as a Java application (not Android).
 * 
 * @author Markus
 */
public class GuitarDaoGen {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(3, "com.guitar.instructor.daos");

        // add song entity
        Entity song = schema.addEntity("Song");
        
        song.addStringProperty("id").primaryKey();
        song.addStringProperty("title");
        song.addStringProperty("lyric");
        song.addIntProperty("rhythm_id");
        Property composerIdProperty = song.addStringProperty("composer_id").getProperty();
        Property singerIdProperty = song.addStringProperty("singer_id").getProperty();
        song.addIntProperty("level_id");
        song.addIntProperty("melody_id");
        song.addStringProperty("update_date");
        //song.addStringProperty("status");
        song.addStringProperty("music_link");
        song.addIntProperty("favorite");
        song.addStringProperty("setting");
        song.addStringProperty("unsigned_title");
        
        // add artist
        Entity artist = schema.addEntity("Artist");
        artist.addStringProperty("id").primaryKey();
        artist.addStringProperty("name");
        artist.addStringProperty("unsigned_name");
        artist.addStringProperty("description");
        artist.addBooleanProperty("is_composer");
        artist.addBooleanProperty("is_guru");
        artist.addBooleanProperty("is_singer");
        artist.addStringProperty("update_date");

        // add level
        Entity level = schema.addEntity("Level");
        level.addStringProperty("id").primaryKey();
        level.addStringProperty("name");
        level.addStringProperty("description");
        
        // add Rhythm
        Entity rhythm = schema.addEntity("Rhythm");
        rhythm.addStringProperty("id").primaryKey();
        rhythm.addStringProperty("name");
        rhythm.addStringProperty("description");
        
        
        // add melody
        Entity melody = schema.addEntity("Melody");
        melody.addStringProperty("id").primaryKey();
        melody.addStringProperty("name");
        melody.addStringProperty("description");
        
     // add ChordLib
        Entity ChordLib = schema.addEntity("ChordLib");
        ChordLib.addStringProperty("id").primaryKey();
        ChordLib.addStringProperty("name");
        ChordLib.addStringProperty("type");
        ChordLib.addStringProperty("data");
        ChordLib.addStringProperty("variation");
        
        
        
     // The variables "user" and "picture" are just regular entities
        song.addToOne(artist, composerIdProperty,"songcomposer");
        song.addToOne(artist, singerIdProperty, "songsinger");

        //new DaoGenerator().generateAll(schema, "../GuitarDao");
        new DaoGenerator().generateAll(schema, "GuitarDao");
    }
    
   
    
//    private static void addNote(Schema schema) {
//        Entity note = schema.addEntity("Note");
//        note.addIdProperty();
//        note.addStringProperty("text").notNull();
//        note.addStringProperty("comment");
//        note.addDateProperty("date");
//    }
//
//    private static void addCustomerOrder(Schema schema) {
//        Entity customer = schema.addEntity("Customer");
//        customer.addIdProperty();
//        customer.addStringProperty("name").notNull();
//
//        Entity order = schema.addEntity("Order");
//        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
//        order.addIdProperty();
//        Property orderDate = order.addDateProperty("date").getProperty();
//        Property customerId = order.addLongProperty("customerId").notNull().getProperty();
//        order.addToOne(customer, customerId);
//
//        ToMany customerToOrders = customer.addToMany(order, customerId);
//        customerToOrders.setName("orders");
//        customerToOrders.orderAsc(orderDate);
//    }

}

