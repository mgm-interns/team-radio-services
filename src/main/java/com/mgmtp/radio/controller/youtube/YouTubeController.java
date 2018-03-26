package com.mgmtp.radio.controller.youtube;

import com.mgmtp.radio.controller.response.RadioResponse;
import com.mgmtp.radio.controller.response.RadioSuccessResponse;
import com.mgmtp.radio.domain.youtube.YouTubeVideo;
import com.mgmtp.radio.dto.youtube.YouTubeSearchCriteriaDTO;
import com.mgmtp.radio.dto.youtube.YouTubeVideoDTO;
import com.mgmtp.radio.exception.RadioBadRequestException;
import com.mgmtp.radio.exception.RadioException;
import com.mgmtp.radio.service.youtube.YouTubeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(YouTubeController.BASE_URL)
public class YouTubeController {
    public static final String BASE_URL = "/api/youtube";

//    private final YouTubeService youTubeService;
//
//    public YouTubeController(YouTubeService youTubeService){
//        this.youTubeService=youTubeService;
//    }

//    @ApiOperation(value = "Search videos", notes = "Return an array of videos")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200,message = "Request processed successfully", response = RadioResponse.class)
//            @ApiResponse(code = 400,message = "Error in request parameters", response = RadioBadRequestException.class)
//            @ApiResponse(code = 500,message = "Server errorServer error", response = RadioException.class)
//    })
//    @PostMapping("/search/{queryTerm}")
//    @ResponseStatus(HttpStatus.OK)
//    public RadioSuccessResponse<List<YouTubeVideoDTO>> fetchVideosByQuery(@PathVariable String queryTerm){
//
//    }
}
