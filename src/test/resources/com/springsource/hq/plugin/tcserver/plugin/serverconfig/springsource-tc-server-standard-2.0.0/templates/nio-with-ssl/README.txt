Version: 6.0.25.A-RELEASE
Build Date: 20100406141135

The nio-with-ssl template includes the following changes from the default tc
Runtime instance:
  - A modified server.xml that uses the Non-Blocking IO (NIO) connector for
    HTTP.
  - A modified server.xml that includes an NIO HTTPS connector.
  - A sample JKS keystore that can be used to test the SSL configuration. The
    sample JKS keystore is not suitable for production systems.