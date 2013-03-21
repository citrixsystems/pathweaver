use loadbalancing;
source ./controller/controller-war/src/deb/contrib/db/seed.sql;

use loadbalancingplugin;
source /pathweaver-lb/core-plugins/core-netscaler-plugin/src/main/resources/core-netscaler-plugin-seed.sql;

use helios_loadbalancing;
insert into tenant (id,name) VALUES('1000', 'tenant1');
insert into tenant (id,name) VALUES('2000', 'tenant2');

