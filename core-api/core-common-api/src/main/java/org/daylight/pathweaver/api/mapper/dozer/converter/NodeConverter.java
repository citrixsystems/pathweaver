package org.daylight.pathweaver.api.mapper.dozer.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.daylight.pathweaver.core.api.v1.Node;
import org.daylight.pathweaver.core.api.v1.Nodes;
import org.daylight.pathweaver.service.domain.exception.NoMappableConstantException;

import org.dozer.CustomConverter;


import java.util.HashSet;
import java.util.Set;


public class NodeConverter implements CustomConverter {
    private final Log logger = LogFactory.getLog(NodeConverter.class);



    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {

        if (sourceFieldValue == null) {
            return null;
        }


        if ((sourceFieldValue instanceof Set)) {

            Set<org.daylight.pathweaver.service.domain.entity.Node> domainNodes = (Set<org.daylight.pathweaver.service.domain.entity.Node>) sourceFieldValue;
            Nodes nodes = new org.daylight.pathweaver.core.api.v1.Nodes();

             try {
                for (org.daylight.pathweaver.service.domain.entity.Node domainNode : domainNodes) {
                    Node node = new Node();
                    node.setId(domainNode.getId());
                    node.setAddress(domainNode.getAddress());
                    node.setEnabled(domainNode.isEnabled());
                    node.setPort(domainNode.getPort());
                    node.setWeight(domainNode.getWeight());
                    node.setStatus(domainNode.getStatus());


                    nodes.getNodes().add(node);

                }
             } catch (NullPointerException e) {
                 //Ignore, there is nothing to map
            }

            return nodes;

        }


        if (sourceFieldValue instanceof Nodes) {
            Nodes nodes = (Nodes) sourceFieldValue;
            HashSet<org.daylight.pathweaver.service.domain.entity.Node> domainNodes = new HashSet<org.daylight.pathweaver.service.domain.entity.Node>();

            for (Node node : nodes.getNodes()) {

                org.daylight.pathweaver.service.domain.entity.Node domainNode = new  org.daylight.pathweaver.service.domain.entity.Node();
                domainNode.setId(node.getId());
                domainNode.setAddress(node.getAddress());
                domainNode.setEnabled(node.isEnabled() == null ? true : node.isEnabled());
                domainNode.setPort(node.getPort());
                domainNode.setWeight(node.getWeight() == null ? 1 : node.getWeight());

                domainNodes.add(domainNode);
            }

            return domainNodes;
        }

        throw new NoMappableConstantException("Cannot map source type: " + sourceClass.getName());
    }

}
