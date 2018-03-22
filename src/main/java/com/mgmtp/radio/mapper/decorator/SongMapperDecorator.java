package com.mgmtp.radio.mapper.decorator;

import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.mapper.station.SongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class SongMapperDecorator implements SongMapper {

	@Autowired
	@Qualifier("delegate")
	private SongMapper delegate;

	@Override
	public SongDTO songToSongDTO(Song song) {
		SongDTO songDTO = delegate.songToSongDTO(song);
		songDTO.setUpVoteCount(song.getUpVoteUserIdList().size());
		songDTO.setDownVoteCount(song.getDownVoteUserIdList().size());
		return songDTO;
	}
}
