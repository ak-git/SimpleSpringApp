module com.ak.spring {
  requires java.logging;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.core;
  requires spring.beans;
  requires spring.web;

  opens com.ak.spring to spring.core, com.fasterxml.jackson.databind;
  exports com.ak.spring;
}