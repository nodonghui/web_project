package com.mysite.web.largeFile;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.web.file.LowCapacityFile;

public interface LargeCapacityFileRepository extends JpaRepository<LargeCapacityFile, Integer> {

}
