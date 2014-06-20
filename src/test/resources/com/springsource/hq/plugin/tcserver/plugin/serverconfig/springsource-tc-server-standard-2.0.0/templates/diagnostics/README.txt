Version: 6.0.25.A-RELEASE
Build Date: 20100406141135

The diagnostics template includes the following changes from the default tc Runtime
instance:
 - A sample JDBC resource configuration that integrates with the request
   diagnostics to report slow queries.
 - The ThreadDiagnosticsValve has been configured at the Engine level to report
   on slow running requests. 