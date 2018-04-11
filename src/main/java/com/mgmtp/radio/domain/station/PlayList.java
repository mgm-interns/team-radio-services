package com.mgmtp.radio.domain.station;

import com.mgmtp.radio.dto.station.SongDTO;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlayList {
    public static PlayList EMPTY_PLAYLIST = new PlayList(Collections.emptyList(), null);

    List<SongDTO> listSong;
    NowPlaying nowPlaying;
}
