package com.ak.spring.data.repository;

import java.util.List;

import com.ak.spring.data.entity.Player;
import com.ak.spring.data.id.RevisionableId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, RevisionableId> {
  @Query("select c from Player c " +
      "where lower(c.firstName) like lower(concat('%', :searchTerm, '%')) " +
      "or lower(c.surName) like lower(concat('%', :searchTerm, '%'))" +
      "or lower(c.lastName) like lower(concat('%', :searchTerm, '%'))")
  List<Player> search(@Param("searchTerm") @NonNull String searchTerm);
}
