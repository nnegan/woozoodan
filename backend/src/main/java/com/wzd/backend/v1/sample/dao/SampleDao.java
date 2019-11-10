package com.wzd.backend.v1.sample.dao;

import com.wzd.backend.v1.sample.model.SampleModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SampleDao {
    SampleModel selectSampleuser(String userId);
}
