package com.mgmtp.radio.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@Data
@Document(collection = "favorite_song")
public class FavoriteSong {
	@Id
	private String id;
	@Indexed
	private String userId;
	@Indexed
	private String songId;
	private LocalDate createdAt;
}
