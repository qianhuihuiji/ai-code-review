package com.nofirst.ai.code.review.mapstruct;

import com.nofirst.ai.code.review.model.gitlab.MyPushEvent;
import org.gitlab4j.api.webhook.PushEvent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {RepositoryMapper.class, CommitMapper.class, ProjectMapper.class, AuthorMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PushEventMapper {

    PushEvent toPushEvent(MyPushEvent myPushEvent);
}
