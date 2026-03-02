package org.example.simplewebresource.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestData implements Serializable {

    @Serial
    private static final long serialVersionUID = -5137211869402277615L;

    private String strVal;
}
