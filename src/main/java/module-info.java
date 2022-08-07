module com.ak.spring {
  requires java.logging;
  requires java.sql;
  requires java.persistence;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.core;
  requires spring.beans;
  requires spring.web;
  requires spring.data.jpa;
  requires spring.data.commons;
  requires org.hibernate.orm.core;
  requires javafaker;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires org.apache.tomcat.embed.core;

  opens com.ak.spring to spring.core, com.fasterxml.jackson.databind;
  opens com.ak.spring.data.entity to spring.core, org.hibernate.orm.core, com.fasterxml.jackson.databind;
  opens com.ak.spring.data to spring.core;
  opens com.ak.spring.data.id to spring.core, spring.beans, org.hibernate.orm.core;
  opens com.ak.spring.controller to spring.core, com.fasterxml.jackson.databind;
  exports com.ak.spring.data to spring.beans;
  exports com.ak.spring.controller to spring.beans, spring.web, com.fasterxml.jackson.databind;
  exports com.ak.spring.data.repository;
  exports com.ak.spring.data.entity;
  exports com.ak.spring;
}