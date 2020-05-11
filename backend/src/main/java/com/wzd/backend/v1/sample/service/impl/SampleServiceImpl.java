package com.wzd.backend.v1.sample.service.impl;

import com.wzd.backend.v1.sample.dao.SampleDao;
import com.wzd.backend.v1.sample.model.SampleModel;
import com.wzd.backend.v1.sample.model.SampleSearchModel;
import com.wzd.backend.v1.sample.service.SampleService;
import com.wzd.common.model.PagenatedListModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class SampleServiceImpl implements SampleService {

    @Autowired
    private SampleDao sampleDao;


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public SampleModel selectSampleuser(String userId) {
        return sampleDao.selectSampleuser(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertUser(SampleModel sampleModel) {
        sampleDao.insertUser(sampleModel);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(SampleModel sampleModel) {
        sampleDao.updateUser(sampleModel);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(String userId) {
        sampleDao.deleteUser(userId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagenatedListModel<SampleModel> selectSampleuserList(SampleSearchModel sampleSearchModel) {
        PagenatedListModel<SampleModel> sml = new PagenatedListModel<>(sampleDao.selectSampleuserList(sampleSearchModel));
        return sml;
    }


}
