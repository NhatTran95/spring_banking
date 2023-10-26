package com.cg.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class CustomerDepositResDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private BigDecimal balance;

    private LocationRegionResDTO locationRegion;

    private List<DepositResDTO> deposits;
}
