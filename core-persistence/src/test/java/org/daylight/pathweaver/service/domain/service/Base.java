package org.daylight.pathweaver.service.domain.service;

import org.junit.After;
import org.junit.Before;
import org.daylight.pathweaver.datamodel.PathweaverTypeHelper;
import org.daylight.pathweaver.service.domain.entity.LoadBalancer;
import org.daylight.pathweaver.service.domain.exception.PersistenceServiceException;
import org.daylight.pathweaver.service.domain.repository.LoadBalancerRepository;
import org.daylight.pathweaver.service.domain.repository.NodeRepository;
import org.daylight.pathweaver.service.domain.stub.StubFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@ContextConfiguration(locations = {"classpath:db-services-test.xml"})
@Service
public class Base {

    @PersistenceContext(unitName = "loadbalancing")
    protected EntityManager entityManager;

    @Autowired
    protected PathweaverTypeHelper pathweaverTypeHelper;

    @Autowired
    protected LoadBalancerService loadBalancerService;

    @Autowired
    protected NodeService nodeService;

    @Autowired
    protected LoadBalancerRepository loadBalancerRepository;

    @Autowired
    protected NodeRepository nodeRepository;

    protected LoadBalancer loadBalancer;

    @Before
    public void setUpMinimalLoadBalancer() {
        loadBalancer = StubFactory.createMinimalDomainLoadBalancer();
    }

    @After
    public void deleteAllLoadBalancers() throws PersistenceServiceException {
        List<LoadBalancer> loadBalancersToDelete = loadBalancerRepository.getByAccountId(loadBalancer.getAccountId());

        for (LoadBalancer lbToDelete : loadBalancersToDelete) {
            loadBalancerService.delete(lbToDelete);
        }
    }
}
