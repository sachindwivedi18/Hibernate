// 
// Decompiled by Procyon v0.5.36
// 

package com.epam.reportportal.service.item;

import java.util.Objects;

public class TestCaseIdEntry
{
    private String id;
    
    public TestCaseIdEntry() {
    }
    
    public TestCaseIdEntry(final String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TestCaseIdEntry that = (TestCaseIdEntry)o;
        return Objects.equals(this.id, that.id);
    }
    
    @Override
    public int hashCode() {
        return (this.id != null) ? this.id.hashCode() : 0;
    }
}
