package com.mgmtp.radio.mapper.decorator;

import com.mgmtp.radio.domain.station.Song;
import com.mgmtp.radio.dto.station.SongDTO;
import com.mgmtp.radio.mapper.station.SongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public abstract class SongMapperDecorator implements SongMapper {

	@Autowired
	@Qualifier("delegate")
	private SongMapper delegate;

	@Override
	public SongDTO songToSongDTO(Song song) {
		SongDTO songDTO = delegate.songToSongDTO(song);
		songDTO.setUpVoteCount(getVoteCount(song.getUpVoteUserIdList()));
		songDTO.setDownVoteCount(getVoteCount(song.getDownVoteUserIdList()));
		return songDTO;
	}

	private int getVoteCount(List<String> voteList) {
		return (voteList == null ? 0 : voteList.size());
	}
}