/*

 */
package com.churchclerk.demoapi;

import com.churchclerk.baseapi.model.BaseModel;

import java.util.Objects;

/**
 *
 */
public class Demo extends BaseModel {
    private String testData;

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Demo)) return false;
        if (!super.equals(o)) return false;
        Demo testModel = (Demo) o;
        return Objects.equals(testData, testModel.testData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), testData);
    }

    /**
     *
     * @param source
     */
    public void copy(Demo source) {
        super.copy(source);
        setTestData(source.getTestData());
    }

    /**
     *
     * @param source
     */
    public void copyNonNulls(Demo source) {
        super.copyNonNulls(source);
        if (source.getTestData() != null) {
            setTestData(source.getTestData());
        }
    }
}
