package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.File;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FileRepository extends CrudRepository<File, Long> {
    Optional<File> findByName(String fileName);
}
