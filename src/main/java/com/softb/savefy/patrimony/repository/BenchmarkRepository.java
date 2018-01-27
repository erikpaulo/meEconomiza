package com.softb.savefy.patrimony.repository;

import com.softb.savefy.patrimony.model.Benchmark;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository("AppBenchmarkRepository")
public interface BenchmarkRepository extends JpaRepository<Benchmark, Integer> {

    @Query("select b from Benchmark b where b.date = :date")
    Benchmark findOneByDate(@Param("date") Date date) throws DataAccessException;

}
