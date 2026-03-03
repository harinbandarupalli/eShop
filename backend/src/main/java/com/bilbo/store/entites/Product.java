package com.bilbo.store.entites;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"category", "images"})
@Entity
@Table(name = "products", schema = "eshop")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @Column(name = "is_trending")
    private Boolean isTrending;

    @Column(name = "created_timestamp", updatable = false)
    private OffsetDateTime createdTimestamp;

    @Column(name = "last_updated_timestamp")
    private OffsetDateTime lastUpdatedTimestamp;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;
}
