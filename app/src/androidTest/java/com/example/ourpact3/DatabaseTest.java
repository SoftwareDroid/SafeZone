package com.example.ourpact3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.ourpact3.db.AppDao;
import com.example.ourpact3.db.AppEntity;
import com.example.ourpact3.db.ExceptionListEntity;
import com.example.ourpact3.db.ExceptionListDao;
import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.TimeRestrictionRuleEntity;
import com.example.ourpact3.db.TimeRestrictionRulesDao;
import com.example.ourpact3.db.UsageFiltersDao;
import com.example.ourpact3.db.UsageFiltersEntity;

import org.junit.Test;

import java.util.List;

public class DatabaseTest
{
    @Test
    public void testAppInfoDao()
    {
        // Get the context
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Create a new instance of the AppsDatabase
        AppsDatabase db = Room.databaseBuilder(context, AppsDatabase.class, "apps-database")
                .allowMainThreadQueries()
                .build();
        // Create Usage Filter
        UsageFiltersDao daoUsageFilters = db.usageFiltersDao();
        AppDao appDao =  db.appsDao();
        // Usage Filter Enity
        UsageFiltersEntity usageFilter1 = new UsageFiltersEntity();
        UsageFiltersEntity usageFilter2 = new UsageFiltersEntity();
        long id1 = daoUsageFilters.insert(usageFilter1);
        long id2 = daoUsageFilters.insert(usageFilter2);
        // Create Sample App Entry
        AppEntity app1 = new AppEntity();
        app1.setUsageFilterId(id1);
        app1.setPackageName("foobar");
        app1.setReadable(true);
        //insert
        assertEquals(0,appDao.getAllApps().size());
        appDao.insertApp(app1);
        assertEquals(1,appDao.getAllApps().size());
        AppEntity retrievedApp =  appDao.getAppByPackageName(app1.getPackageName());
        assertEquals(retrievedApp.getPackageName(),app1.getPackageName());
        assertEquals(retrievedApp.getReadable(),app1.getReadable());
        assertEquals(retrievedApp.getUsageFilterId(),app1.getUsageFilterId());
        appDao.deleteApp(app1);
        assertEquals(0,appDao.getAllApps().size());
        // Test time restriction rules
        // add some restrictions for some apps
        TimeRestrictionRuleEntity timeRestriction1 = new TimeRestrictionRuleEntity();
        timeRestriction1.setMonday(true);
        timeRestriction1.setStartHour(7);
        timeRestriction1.setStartMin(30);
        timeRestriction1.setEndHour(18);
        timeRestriction1.setUsageFilterId(id1);
        TimeRestrictionRuleEntity timeRestriction2 = new TimeRestrictionRuleEntity();
        TimeRestrictionRuleEntity timeRestriction3 = new TimeRestrictionRuleEntity();
        timeRestriction2.setUsageFilterId(id2);
        timeRestriction3.setUsageFilterId(id2);
        TimeRestrictionRulesDao timeRestrictionsDao = db.timeRestrictionRulesDao();
        assertEquals(0,timeRestrictionsDao.getRulesForUsageFilter(id2).size());
        timeRestrictionsDao.insert(timeRestriction1,timeRestriction2,timeRestriction3);
        assertEquals(2,timeRestrictionsDao.getRulesForUsageFilter(id2).size());
        assertEquals(1,timeRestrictionsDao.getRulesForUsageFilter(id1).size());
        timeRestrictionsDao.deleteRulesForUsageFilter(2);
        assertEquals(0,timeRestrictionsDao.getRulesForUsageFilter(id2).size());

    }

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

        // Create a new ExceptionList entity
        ExceptionListEntity exceptionList2 = new ExceptionListEntity();
        exceptionList2.setAppName("example2");
        exceptionList2.setReadable(true);
        exceptionList2.setWritable(false);

        ExceptionListEntity exceptionList3 = new ExceptionListEntity();
        exceptionList3.setAppName("example3");


        // Insert the ExceptionList entity into the database
        ExceptionListDao dao = db.exceptionListDao();
        dao.insert(exceptionList);
        dao.insert(exceptionList2);
        dao.insert(exceptionList3);

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

        List<ExceptionListEntity> allExceptions = dao.getAll();
        // we delete one two should remain
        assertEquals(2,allExceptions.size());
    }

}
