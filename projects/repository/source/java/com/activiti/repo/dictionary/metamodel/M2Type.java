package com.activiti.repo.dictionary.metamodel;

import java.util.List;


/**
 * Type Definition.
 * 
 * @author David Caruana
 */
public interface M2Type extends M2Class
{
    
    public List getDefaultAspects();

    // TODO: Investigate this option - will we support?, is it in right place?
    public boolean getOrderedChildren();
    
    public void setOrderedChildren(boolean areChildrenOrdered);
       
}
