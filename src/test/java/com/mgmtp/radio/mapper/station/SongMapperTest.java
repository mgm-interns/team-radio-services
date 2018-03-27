package com.mgmtp.radio.mapper.station;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.dto.station.SongDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class SongMapperTest {
    @Autowired
    @Qualifier("songMapperImpl")
    private SongMapper songMapper;

    private final static boolean IS_PLAYED = false;
    private final static boolean IS_SKIPPED = false;
    private final static String URL = "https://www.youtube.com/watch?v=uNgE8pz9eq0";
    private final static String TITLE = "";
    private final static String THUMBNAIL = "";
    private final static int DURATION = 50000;
    private final static String MESSAGE = "This is for you.";

    @Test
    public void userToUserDTO() {
        Song song = new Song();
        song.setPlaying(IS_PLAYED);
        song.setSkipped(IS_SKIPPED);
        song.setUrl(URL);
        song.setTitle(TITLE);
        song.setThumbnail(THUMBNAIL);
        song.setDuration(DURATION);
        song.setMessage(MESSAGE);

        SongDTO songDTO = songMapper.songToSongDTO(song);

        assertEquals(song.isPlaying(), songDTO.isPlaying());
        assertEquals(song.isSkipped(), songDTO.isSkipped());
        assertEquals(song.getUrl(), songDTO.getUrl());
        assertEquals(song.getTitle(), songDTO.getTitle());
        assertEquals(song.getThumbnail(), songDTO.getThumbnail());
        assertEquals(song.getDuration(), songDTO.getDuration());
        assertEquals(song.getMessage(), songDTO.getMessage());
    }

    @Test
    public void userDTOToUser() {
        SongDTO songDTO = new SongDTO();
        songDTO.setPlaying(IS_PLAYED);
        songDTO.setSkipped(IS_SKIPPED);
        songDTO.setUrl(URL);
        songDTO.setTitle(TITLE);
        songDTO.setThumbnail(THUMBNAIL);
        songDTO.setDuration(DURATION);
        songDTO.setMessage(MESSAGE);

        Song song = songMapper.songDtoToSong(songDTO);

        assertEquals(songDTO.isPlaying(), song.isPlaying());
        assertEquals(songDTO.isSkipped(), song.isSkipped());
        assertEquals(songDTO.getUrl(), song.getUrl());
        assertEquals(songDTO.getTitle(), song.getTitle());
        assertEquals(songDTO.getThumbnail(), song.getThumbnail());
        assertEquals(songDTO.getDuration(), song.getDuration());
        assertEquals(songDTO.getMessage(), song.getMessage());
    }
}
