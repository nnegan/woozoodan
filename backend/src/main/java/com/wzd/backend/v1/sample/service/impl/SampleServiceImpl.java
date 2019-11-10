package com.wzd.backend.v1.sample.service.impl;

import com.wzd.backend.v1.sample.dao.SampleDao;
import com.wzd.backend.v1.sample.model.SampleModel;
import com.wzd.backend.v1.sample.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
//@Transactional(rollbackFor = Throwable.class)
public class SampleServiceImpl implements SampleService {

    @Autowired
    private SampleDao sampleDao;
/*
    public SampleServiceImpl(SampleDao sampleDao) {
        this.sampleDao = sampleDao;
    }*/

    @Override
    public SampleModel selectSampleuser(String userId) {
        return sampleDao.selectSampleuser(userId);
    }
}
