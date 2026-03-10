package com.bilbo.store.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Immutable snapshot of a product bag at the time of order placement.
 * Captures name and price so that future data changes don't alter history.
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "order", "bag" })
@Entity
@Table(name = "order_bag_snapshots", schema = "eshop")
public class OrderBagSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "bag_id", nullable = false)
    private ProductBag bag;

    /** Snapshot of the bag name at the time of purchase. */
    @Column(name = "bag_name", nullable = false)
    private String bagName;

    @Column(nullable = false)
    private Integer quantity;

    /** Snapshot of the bag price at the time of purchase. */
    @Column(name = "price_at_purchase", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtPurchase;

    @Column(name = "created_timestamp", updatable = false)
    private OffsetDateTime createdTimestamp;
}
