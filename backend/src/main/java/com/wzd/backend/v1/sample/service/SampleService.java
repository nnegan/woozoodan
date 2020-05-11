package com.wzd.backend.v1.sample.service;

import com.wzd.backend.v1.sample.model.SampleModel;
import com.wzd.backend.v1.sample.model.SampleSearchModel;
import com.wzd.common.model.PagenatedListModel;

public interface SampleService {

    SampleModel selectSampleuser(String userId);

    void insertUser (SampleModel sampleModel);

    void updateUser (SampleModel sampleModel);

    void deleteUser (String userId);

    PagenatedListModel<SampleModel> selectSampleuserList(SampleSearchModel sampleSearchModel);
}
