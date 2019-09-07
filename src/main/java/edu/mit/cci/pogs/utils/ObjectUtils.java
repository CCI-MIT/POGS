package edu.mit.cci.pogs.utils;

import org.apache.commons.beanutils.PropertyUtilsBean;

public class ObjectUtils {
    public static void Copy(Object destination, Object source){
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            propertyUtilsBean.copyProperties(destination, source);
        }
        catch (Exception e){
            System.out.println("Properties mismatch in Beans");
        }
    }
}
