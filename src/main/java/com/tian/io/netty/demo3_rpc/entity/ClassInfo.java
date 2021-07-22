package com.tian.io.netty.demo3_rpc.entity;

import java.io.Serializable;

/**
 * @author David Tian
 * @desc
 * @since 2020-04-21 13:38
 */
public class ClassInfo implements Serializable {

    private String className;
    private String methodName;
    private Object[] args;
    private Class[] classType;

    public ClassInfo() {
    }

    public ClassInfo(String className, String methodName, Object[] args, Class[] classType) {
        this.className = className;
        this.methodName = methodName;
        this.args = args;
        this.classType = classType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Class[] getClassType() {
        return classType;
    }

    public void setClassType(Class[] classType) {
        this.classType = classType;
    }
}
