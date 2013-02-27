use loadbalancing;
source /home/youcef/projects/radware-test/pathweaver-lb/core-api/core-public-web/src/deb/contrib/db/core-seed.sql;

use loadbalancingadapter;
source /home/youcef/projects/radware-test/pathweaver-lb/core-adapters/core-netscaler-adapter/src/main/resources/core-netscaler-adapter-seed.sql;

use helios_loadbalancing;
insert into tenant (id,name) VALUES('1000', 'tenant1');
insert into tenant (id,name) VALUES('2000', 'tenant2');

