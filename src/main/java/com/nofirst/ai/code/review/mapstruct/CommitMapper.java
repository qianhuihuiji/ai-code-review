package com.nofirst.ai.code.review.mapstruct;

import com.nofirst.ai.code.review.model.gitlab.MyCommit;
import org.gitlab4j.api.models.Commit;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommitMapper {

    Commit toCommit(MyCommit myCommit);
}
