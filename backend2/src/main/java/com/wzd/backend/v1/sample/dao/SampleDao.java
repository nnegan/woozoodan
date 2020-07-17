package com.wzd.backend.v1.sample.dao;

import java.util.List;

import com.wzd.backend.v1.sample.model.SampleModel;
import com.wzd.backend.v1.sample.model.SampleSearchModel;
import com.wzd.common.db.mysql.annotation.Dao;

@Dao
public interface SampleDao {
    SampleModel selectSampleuser(String userId);

    void insertUser(SampleModel sampleModel);

    void updateUser(SampleModel sampleModel);

    void deleteUser(String userId);

    List<SampleModel> selectSampleuserList(SampleSearchModel sampleSearchModel);
}
