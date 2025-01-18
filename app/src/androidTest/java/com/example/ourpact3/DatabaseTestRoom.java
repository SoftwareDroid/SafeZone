package com.example.ourpact3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ourpact3.db.ExceptionListEntity;
import com.example.ourpact3.db.ExceptionListDao;
import com.example.ourpact3.db.AppsDatabase;

import org.junit.Test;

public class DatabaseTestRoom
{
    @Test
    public void testExceptionListDao() {
        // Get the context
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Create a new instance of the AppsDatabase
        AppsDatabase db = Room.databaseBuilder(context, AppsDatabase.class, "apps-database")
                .allowMainThreadQueries()
                .build();

        // Create a new ExceptionList entity
        ExceptionListEntity exceptionList = new ExceptionListEntity();
        exceptionList.setAppName("example");
        exceptionList.setReadable(true);
        exceptionList.setWritable(false);

        // Insert the ExceptionList entity into the database
        ExceptionListDao dao = db.exceptionListDao();
        dao.insert(exceptionList);

        // Retrieve the ExceptionList entity from the database
        ExceptionListEntity retrievedExceptionList = dao.getExceptionListByAppName("example");

        // Assert that the retrieved data is not null
        assertNotNull(retrievedExceptionList);

        // Assert that the retrieved data matches the inserted data
        assertEquals(exceptionList.getAppName(), retrievedExceptionList.getAppName());
        assertEquals(exceptionList.getReadable(), retrievedExceptionList.getReadable());
        assertEquals(exceptionList.getWritable(), retrievedExceptionList.getWritable());

        // Delete the ExceptionList entity from the database
        dao.delete(exceptionList);

        // Retrieve the ExceptionList entity from the database again
        ExceptionListEntity deletedExceptionList = dao.getExceptionListByAppName("example");
        // Assert that the retrieved data is null
        assertNull(deletedExceptionList);
    }

}
