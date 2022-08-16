package com.ak.spring.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ak.spring.data.entity.Person;
import com.ak.spring.data.id.RevisionableId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, RevisionableId> {
  @Query("select p from Person p where p.uuid = :uuid order by p.revision desc nulls last")
  @NonNull
  List<Person> historyForUUID(@Param("uuid") @NonNull UUID uuid);

  /**
   * Based on <a href="http://sqlfiddle.com/#!9/a6c585/1">SQL Fiddle</a>
   * and answer <a href="https://stackoverflow.com/a/7745635/808921">SQL select only rows with max value on a column</a>
   *
   * @param uuid identification
   * @return single Player
   */
  @Query("select a from Person a left outer join Person b ON a.uuid = b.uuid AND a.revision < b.revision where b.uuid is null and a.uuid = :uuid " +
      "and (NULLIF(a.name, '') IS NOT NULL or NULLIF(a.password, '') IS NOT NULL)")
  @NonNull
  Optional<Person> findByUUID(@Param("uuid") @NonNull UUID uuid);

  /**
   * Based on <a href="http://sqlfiddle.com/#!9/a6c585/1">SQL Fiddle</a>
   * and answer <a href="https://stackoverflow.com/a/7745635/808921">SQL select only rows with max value on a column</a>
   *
   * @return list of the latest Person's versions
   */
  @Query("select a from Person a left outer join Person b ON a.uuid = b.uuid AND a.revision < b.revision where b.uuid is null " +
      "and (NULLIF(a.name, '') IS NOT NULL or NULLIF(a.password, '') IS NOT NULL)" +
      "order by a.revision desc nulls last")
  @NonNull
  List<Person> findAllPersons();
}
