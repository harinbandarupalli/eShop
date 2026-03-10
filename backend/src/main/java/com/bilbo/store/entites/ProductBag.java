package com.bilbo.store.entites;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@ToString(exclude = { "products", "categories" })
@Entity
@Table(name = "product_bags", schema = "eshop")
public class ProductBag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Admin-set display price. If null, the application derives it
     * from the sum of the member products' prices.
     */
    @Column(name = "display_price", precision = 10, scale = 2)
    private BigDecimal displayPrice;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_timestamp", updatable = false)
    private OffsetDateTime createdTimestamp;

    @Column(name = "last_updated_timestamp")
    private OffsetDateTime lastUpdatedTimestamp;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    /** Products that belong to this bag (many-to-many). */
    @ManyToMany
    @JoinTable(name = "product_bag_products", schema = "eshop", joinColumns = @JoinColumn(name = "bag_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;

    /** Categories this bag belongs to (many-to-many). */
    @ManyToMany
    @JoinTable(name = "product_bag_categories", schema = "eshop", joinColumns = @JoinColumn(name = "bag_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<ProductCategory> categories;
}
