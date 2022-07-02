module com.ak.app {
  requires java.logging;
  requires jsr305;

  opens com.ak.app to org.testng;
  exports com.ak.app;
}