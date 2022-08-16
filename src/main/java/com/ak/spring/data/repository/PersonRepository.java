package com.ak.spring.data.repository;

import com.ak.spring.data.entity.Person;
import com.ak.spring.data.id.RevisionableId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, RevisionableId> {

}
