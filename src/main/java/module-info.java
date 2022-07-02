module com.ak.spring {
  requires java.logging;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.core;
  requires spring.beans;

  opens com.ak.spring to spring.core, org.junit.jupiter;
  exports com.ak.spring;
}