package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TimeRestrictionRulesDao {
    @Insert
    long insert(TimeRestrictionRulesEntity... rules);

    @Delete
    void delete(TimeRestrictionRulesEntity rule);

    @Query("SELECT * FROM time_restriction_rules")
    List<TimeRestrictionRulesEntity> getAll();

    @Query("SELECT * FROM time_restriction_rules WHERE usage_filter_id = :usageFilterId")
    List<TimeRestrictionRulesEntity> getRulesForUsageFilter(int usageFilterId);
}
