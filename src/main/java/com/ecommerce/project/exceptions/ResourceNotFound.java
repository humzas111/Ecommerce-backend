package com.ecommerce.project.exceptions;

public class ResourceNotFound extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;

    public ResourceNotFound(String field,String resourceName, String fieldName) {
        super(String.format("%s not found with %s: %s", resourceName, field, fieldName));

        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }
    public ResourceNotFound (String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d", resourceName, field, field));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
}
