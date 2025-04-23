package com.nofirst.ai.code.review.mapstruct;

import com.nofirst.ai.code.review.model.gitlab.MyAuthor;
import org.gitlab4j.api.models.Author;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {

    Author toAuthor(MyAuthor myAuthor);
}
