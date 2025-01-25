package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TimeRestrictionRulesDao {
    @Insert
    void insert(TimeRestrictionRuleEntity... rules);

    @Delete
    void delete(TimeRestrictionRuleEntity rule);

    @Query("SELECT * FROM time_restriction_rules")
    List<TimeRestrictionRuleEntity> getAll();

    @Query("SELECT * FROM time_restriction_rules WHERE usage_filter_id = :usageFilterId")
    List<TimeRestrictionRuleEntity> getRulesForUsageFilter(long usageFilterId);

    @Query("DELETE FROM time_restriction_rules WHERE usage_filter_id = :usageFilterId")
    void deleteRulesForUsageFilter(long usageFilterId);
}
