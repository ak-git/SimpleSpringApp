package com.ak.spring.data.repository;

import java.util.List;
import java.util.UUID;

import com.ak.spring.data.entity.Player;
import com.ak.spring.data.id.RevisionableId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, RevisionableId> {
  @Query("select p from Player p where p.uuid = :uuid order by p.revision desc nulls last")
  @NonNull
  List<Player> historyForUUID(@Param("uuid") @NonNull UUID uuid);

  /**
   * Based on <a href="http://sqlfiddle.com/#!9/a6c585/1">SQL Fiddle</a>
   * and answer <a href="https://stackoverflow.com/a/7745635/808921">SQL select only rows with max value on a column</a>
   *
   * @return list of the latest Player's versions
   */
  @Query("select a from Player a left outer join Player b ON a.uuid = b.uuid AND a.revision < b.revision where b.uuid is null " +
      "and a.owner.uuid = :uuid " +
      "and (NULLIF(a.firstName, '') IS NOT NULL or NULLIF(a.surName, '') IS NOT NULL or NULLIF(a.lastName, '') IS NOT NULL) " +
      "order by a.revision desc nulls last")
  @NonNull
  List<Player> findAllPlayers(@Param("uuid") @NonNull UUID uuid);
}
