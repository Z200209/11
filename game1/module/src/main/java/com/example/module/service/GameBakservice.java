package com.example.module.service;

import com.example.module.entity.listDTO;
import com.example.module.mapper.GameBakMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class GameBakservice {
    @Resource
    private GameBakMapper mapper;

    public List<listDTO> getAllGame(Integer page, Integer pageSize, String keyword, BigInteger typeId) {
        return mapper.getAll((page-1) * pageSize, pageSize, keyword,typeId);
    }

}
