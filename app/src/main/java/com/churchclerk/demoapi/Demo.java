/*

 */
package com.churchclerk.demoapi;

import com.churchclerk.baseapi.model.BaseModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 *
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class Demo extends BaseModel {
    private String testData;


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
        copy(source.getTestData(), this::setTestData);
    }
}
