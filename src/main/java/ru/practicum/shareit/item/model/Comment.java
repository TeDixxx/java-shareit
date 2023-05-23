package ru.practicum.shareit.item.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty
    @NotBlank
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private LocalDateTime created;
}
