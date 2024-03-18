package com.ssafy.jariyo.domain.playroom.service;

import com.ssafy.jariyo.domain.board.entity.Board;
import com.ssafy.jariyo.domain.playroom.dto.PlayroomRequestUpdateDto;
import com.ssafy.jariyo.domain.playroom.dto.PlayroomResponseGetDto;
import com.ssafy.jariyo.domain.playroom.dto.PlayroomResponseGetListDto;
import com.ssafy.jariyo.domain.playroom.entity.Playroom;
import com.ssafy.jariyo.domain.playroom.repository.PlayroomRepository;
import com.ssafy.jariyo.domain.s3image.service.S3ImageService;
import com.ssafy.jariyo.domain.store.entity.Store;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlayroomService {
    private final PlayroomRepository playroomRepository;
    private final S3ImageService s3ImageService;

    public void createPlayroom(Store store) {
        Playroom playroom = new Playroom(
                store,
                "title",
                null,
                "info",
                0,
                "justChat",
                false,
                false,
                false,
                false
        );
        playroomRepository.save(playroom);
    }

    public PlayroomResponseGetDto getPlayroomInfo(Long StoreId) {
        Playroom playroom = playroomRepository.findByStore_StoreId(StoreId);
        return toDto(playroom);
    }

    public PlayroomResponseGetListDto searchPlayroomsWithKeywordAndCategory(String keyword, String category, Pageable pageable) {
        Page<Playroom> playrooms = playroomRepository.search(keyword, category, pageable);

        PlayroomResponseGetListDto playroomResponseGetListDto = new PlayroomResponseGetListDto();
        playroomResponseGetListDto.setList(toDtoList(playrooms.getContent()));
        playroomResponseGetListDto.setTotalPages(playrooms.getTotalPages());
        playroomResponseGetListDto.setTotalElements(playrooms.getTotalElements());

        return playroomResponseGetListDto;
    }

    public void updatePlayroomInfo(Long userId, Long storeId, PlayroomRequestUpdateDto playroomRequestUpdateDto) {
        Playroom playroom = playroomRepository.findByStore_StoreId(storeId);

        if (playroom != null) {

//            Long ownerId = playroom.getStore().getUser().getUser;

//            if (!userId.equals(ownerId)) {
//                throw new RuntimeException("Unauthorized to update Playroom with StoreId: " + storeId);
//            }

            if (playroomRequestUpdateDto.getTitle() != null) {
                playroom.setTitle(playroomRequestUpdateDto.getTitle());
            }
            if (playroomRequestUpdateDto.getImage() != null) {
                playroom.setImage(playroomRequestUpdateDto.getImage());
            }
            if (playroomRequestUpdateDto.getInfo() != null) {
                playroom.setInfo(playroomRequestUpdateDto.getInfo());
            }
            if (playroomRequestUpdateDto.getUserCount() != null) {
                playroom.setUserCount(playroomRequestUpdateDto.getUserCount());
            }
            if (playroomRequestUpdateDto.getCategory() != null) {
                playroom.setCategory(playroomRequestUpdateDto.getCategory());
            }
            if (playroomRequestUpdateDto.getChatting() != null) {
                playroom.setChatting(playroomRequestUpdateDto.getChatting());
            }
            if (playroomRequestUpdateDto.getCalling() != null) {
                playroom.setCalling(playroomRequestUpdateDto.getCalling());
            }
            if (playroomRequestUpdateDto.getBroadcasting() != null) {
                playroom.setBroadcasting(playroomRequestUpdateDto.getBroadcasting());
            }
            if (playroomRequestUpdateDto.getWaiting() != null) {
                playroom.setWaiting(playroomRequestUpdateDto.getWaiting());
            }

            playroomRepository.save(playroom);
        } else {
            throw new RuntimeException("Playroom not found with StoreId: " + storeId);
        }
    }

    public void deletePlayroom(Long StoreId) {

        // 상점이 삭제될 때 호출

        Playroom playroom = playroomRepository.findByStore_StoreId(StoreId);

        if (playroom != null) {
            playroom.setDeleted(true);
            playroomRepository.save(playroom);
        } else {
            throw new RuntimeException("Playroom not found with StoreId: " + StoreId);
        }
    }

    public static PlayroomResponseGetDto toDto(Playroom playroom) {
        PlayroomResponseGetDto dto = new PlayroomResponseGetDto();

        dto.setStoreId(playroom.getStore().getStoreId());
        dto.setTitle(playroom.getTitle());
        dto.setImage(playroom.getImage());
        dto.setInfo(playroom.getInfo());
        dto.setUserCount(playroom.getUserCount());
        dto.setCategory(playroom.getCategory());
        dto.setModDate(playroom.getModDate());

        dto.setUserId(playroom.getStore().getUser().getUserId());
        dto.setUserNickname(playroom.getStore().getUser().getNickname());
        dto.setUserImage(playroom.getStore().getUser().getImageUrl());

        dto.setChatting(playroom.getChatting());
        dto.setCalling(playroom.getCalling());
        dto.setBroadcasting(playroom.getBroadcasting());
        dto.setWaiting(playroom.getWaiting());

        return dto;
    }

    public static List<PlayroomResponseGetDto> toDtoList(List<Playroom> playrooms) {
        if (playrooms == null) {
            return null;
        }

        return playrooms.stream()
                .map(PlayroomService::toDto)
                .collect(Collectors.toList());
    }

    public void updateTumbnail(Long storeId, MultipartFile file) {
        Playroom playroom = playroomRepository.findByStore_StoreId(storeId);
        playroom.setImage(s3ImageService.uploadTumbnail(file));
    }
}