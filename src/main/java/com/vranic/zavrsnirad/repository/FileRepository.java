package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository <File, Long> {
    @Query("SELECT f FROM File f WHERE f.fileName LIKE CONCAT(:fileName, '%')")
    List<File> findByFileName(@Param("fileName") String fileName);
}
