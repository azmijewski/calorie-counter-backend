package com.sda.caloriecounterbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] bytes;
    private String name;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();

    public Photo(byte[] bytes) {
        this.bytes = bytes;
    }
}
