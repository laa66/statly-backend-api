package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.Genre;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TopGenresDTO {

    private List<Genre> genres;
    private String range;
}
