package com.example.javawebapp.pojo;

import lombok.Data;
import java.util.List;

@Data
public class SymbolSearchPojo {

    private List<SymbolPojo> bestMatches;

}
