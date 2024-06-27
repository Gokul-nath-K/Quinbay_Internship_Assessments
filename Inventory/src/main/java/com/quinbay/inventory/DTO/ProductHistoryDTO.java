package com.quinbay.inventory.DTO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.quinbay.inventory.model.Product;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductHistoryDTO {

    private String newValue;
    private String oldValue;
    private String column;
    private String productCode;
}
