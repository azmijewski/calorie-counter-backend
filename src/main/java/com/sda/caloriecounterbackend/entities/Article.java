package com.sda.caloriecounterbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@NamedQueries({
        @NamedQuery(name = "article.FindAllOrderByDate", query = "SELECT a from Article a order by a.date")
})
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "text")
    private String content;

    @ManyToOne
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @CreationTimestamp
    private Date date;

}
