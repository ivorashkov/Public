package com.example.web.model.entity;

import com.example.web.model.interfaces.DeletableObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tour_offer_file")
public class TourOfferFilePathEntity extends BaseEntity implements DeletableObject {

  @Column(name = "picture_uri", nullable = false, unique = false)
  private String documentLocation;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "offer_id", nullable = false)
  private TourOfferEntity offer;

}
