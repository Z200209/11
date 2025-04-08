package com.example.module.service.Game;

import com.example.module.entity.GameListDTO;
import com.example.module.mapper.GameBakMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class GameBakservice {
    @Resource
    private GameBakMapper mapper;

    public List<GameListDTO> getAllGame(Integer page, Integer pageSize, String keyword, BigInteger typeId) {
        return mapper.getAll((page-1) * pageSize, pageSize, keyword,typeId);
    }

}
