package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.File;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<File, Long> {
}
