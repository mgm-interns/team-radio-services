package com.mgmtp.radio.domain.station;

import com.mgmtp.radio.dto.station.SongDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PlayList {
    List<SongDTO> listSong;
    NowPlaying nowPlaying;
}
