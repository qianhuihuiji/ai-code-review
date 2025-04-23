package com.nofirst.ai.code.review.mapstruct;

import com.nofirst.ai.code.review.model.gitlab.MyRepository;
import org.gitlab4j.api.models.Repository;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RepositoryMapper {

    Repository toRepository(MyRepository myRepository);
}
