Version: 6.0.25.A-RELEASE
Build Date: 20100406141135

The apr-with-ssl template includes the following changes from the default tc
Runtime instance:
  - A modified server.xml then includes the APRLifecycleListener to detect the
    APR based native library required to use the APR/native connector. 
  - A modified server.xml that uses the APR/native (APR) connector for
    HTTP.
  - A modified server.xml that includes an APR HTTPS connector.
  - Sample certificate and key files that can be used to test the SSL
    configuration. The sample certificate and key files are not suitable for
    production systems.

NOTE: That the APR/native library must be present in order to use the APR
      connector.