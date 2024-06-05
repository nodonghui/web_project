package com.mysite.web.file;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LowCapacityFileRepository extends JpaRepository<LowCapacityFile, Integer> {

}
