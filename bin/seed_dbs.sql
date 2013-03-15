use loadbalancing;
source /home/youcef/projects/radware-test/pathweaver-lb/core-api/core-public-web/src/deb/contrib/db/core-seed.sql;

use loadbalancingplugin;
source /home/youcef/projects/radware-test/pathweaver-lb/core-plugins/core-netscaler-plugin/src/main/resources/core-netscaler-plugin-seed.sql;

use helios_loadbalancing;
insert into tenant (id,name) VALUES('1000', 'tenant1');
insert into tenant (id,name) VALUES('2000', 'tenant2');

