package com.example.web.model.entity;

import com.example.web.model.enums.TransportType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "offers")
public class TourOfferEntity extends BaseEntity {

  @Column(name = "title")
  private String title;

  @Column(name = "is_deleted", nullable = false)
  @ColumnDefault("false")
  private boolean isDeleted;

  @Column(name = "creation_date")
  private LocalDateTime date;

  @Column(nullable = false, name = "country")
  private String country;

  @Column(nullable = false, name = "city")
  private String city;

  @Column(nullable = false, name = "duration_in_days")
  @Min(1)
  @Max(365)
  private int duration;

  @Column(nullable = false, name = "stars")
  @Min(0)
  @Max(7)
  private double stars;

  @Column(nullable = false)
  @Min(1)
  private BigDecimal price;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column
  private double discount;


  @ManyToOne(fetch = FetchType.LAZY)
  private UserEntity user;

  @Column(name = "transport_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private TransportType transportType;

  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "offer"
  )
  private List<TourOfferImagePathEntity> paths;

}
