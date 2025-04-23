package com.nofirst.ai.code.review.mapstruct;

import com.nofirst.ai.code.review.model.gitlab.MyProject;
import org.gitlab4j.api.models.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    Project toProject(MyProject myProject);
}
