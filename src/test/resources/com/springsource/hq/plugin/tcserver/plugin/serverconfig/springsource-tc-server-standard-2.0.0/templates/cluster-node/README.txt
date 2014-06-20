Version: 6.0.25.A-RELEASE
Build Date: 20100406141135

The cluster-node template includes the following changes from the default tc
Runtime instance:
 - Addition of the jvmRoute attribute to the Engine element to uniquely identify
   the node. This is parameterised using ${tcserver.node}
 - Addition of the default Cluster configuration (minus the WAR farm deployer)
   at the Engine level. By default, multicast discovery will be used to identify
   other nodes in the cluster. If multicast is not enabled on the sub-net or if
   multiple tc Runtime clusters may be present on the same subnet then this
   should be re-configured to use static membership.