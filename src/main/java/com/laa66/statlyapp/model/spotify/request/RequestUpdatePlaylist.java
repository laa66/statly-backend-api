package com.laa66.statlyapp.model.spotify.request;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestUpdatePlaylist {

    private List<String> uris;

    private int position;
}
