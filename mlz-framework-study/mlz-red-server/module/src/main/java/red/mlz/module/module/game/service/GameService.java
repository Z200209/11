package red.mlz.module.module.game.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import red.mlz.module.module.game.entity.Game;
import red.mlz.module.module.game.entity.Type;
import red.mlz.module.module.game.mapper.GameMapper;
import red.mlz.module.utils.BaseUtils;


@Service
@Slf4j
public class GameService {

    @Resource
    private GameMapper mapper;
    @Resource
    private TypeService typeService;

    public Game getById(BigInteger id) {
        return mapper.getById(id);
    }
    
    public Game extractById(BigInteger id) {
        return mapper.extractById(id);
    }
    
    public BigInteger insert(Game game) {
        mapper.insert(game);
        return game.getId();
    }
    
    public int update(Game game) {
        return mapper.update(game);
    }
    
    public void unsafeUpdate(Game game) {
        int result = mapper.update(game);
        if(result == 0){
            throw new RuntimeException("update error");
        }
    }
    
    public int delete(BigInteger id) {
        if (BaseUtils.isEmpty(id)){
            throw new RuntimeException("id 不能为空");
        }
        int time = BaseUtils.currentSeconds();
        return mapper.delete(id, time);
    }

    public List<Game> getAllGame(Integer page, Integer pageSize, String keyword, BigInteger typeId) {
        if (BaseUtils.isEmpty(page)) {
            page = 1;
        }
        if (BaseUtils.isEmpty(pageSize)) {
            pageSize = 10;
        }
        // 获取类型ID列表
        String ids = "";
        if (!BaseUtils.isEmpty(keyword)) {
            List<BigInteger> typeIdList = typeService.getTypeIdList(keyword);

            if (!BaseUtils.isEmpty(typeIdList)) {
                StringBuffer typeIdString = new StringBuffer();
                for (BigInteger bigInteger : typeIdList) {
                    if (typeIdString.length() > 0) {
                        typeIdString.append(",");
                    }
                    typeIdString.append(bigInteger.toString());
                }
                ids = typeIdString.toString();
            }
        }

        // 无论是否找到类型ID，都执行查询
        return mapper.getAll((page-1) * pageSize, pageSize, keyword, typeId, ids);
    }

    public List<Game> getAllGameByTypeId(BigInteger typeId) {
        if (BaseUtils.isEmpty(typeId)) {
            return new ArrayList<>();
        }
        return mapper.getAllGameByTypeId(typeId);
    }

    @Transactional(rollbackFor = Exception.class)
    public BigInteger edit(BigInteger id, String gameName, Float price, String gameIntroduction, 
                           String gameDate, String gamePublisher, String images, BigInteger typeId) {
        if (BaseUtils.isEmpty(gameName)) {
            throw new RuntimeException("gameName 不能为空");
        }
        if (BaseUtils.isEmpty(price) || price < 0) {
            throw new RuntimeException("price 不能为空或小于0");
        }
        if (BaseUtils.isEmpty(gameIntroduction)) {
            throw new RuntimeException("gameIntroduction 不能为空");
        }
        if (BaseUtils.isEmpty(gameDate)) {
            throw new RuntimeException("gameDate 不能为空");
        }
        if (BaseUtils.isEmpty(gamePublisher)) {
            throw new RuntimeException("gamePublisher 不能为空");
        }
        if (BaseUtils.isEmpty(images)) {
            throw new RuntimeException("images 不能为空");
        }
        
        if (!BaseUtils.isEmpty(typeId)) {
            Type type = typeService.getById(typeId);
            if (BaseUtils.isEmpty(type)){
                throw new RuntimeException("typeId不存在");
            }
        }

        int time = BaseUtils.currentSeconds();
        Game game = new Game();
        game.setGameName(gameName);
        game.setPrice(price);
        game.setGameIntroduction(gameIntroduction);
        game.setGameDate(gameDate);
        game.setGamePublisher(gamePublisher);
        game.setImages(images);
        game.setUpdateTime(time);
        game.setTypeId(typeId);

        if (BaseUtils.isEmpty(id)) {
            game.setCreateTime(time);
            game.setIsDeleted(0);
            insert(game);
            log.info("新增游戏: {}", game.getGameName());
        } else {
            game.setId(id);
            try {
                unsafeUpdate(game);
                log.info("更新游戏: {}", game.getGameName());
            } catch (Exception e) {
                log.error("更新游戏失败: {}", e.getMessage());
                throw new RuntimeException("更新游戏失败");
            }
        }
        
        return game.getId();
    }

    public Integer getTotalCount(String keyword) {
        return mapper.getTotalCount(keyword);
    }
}
