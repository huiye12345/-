package com.community.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.Reward;

@Mapper
public interface RewardMapper extends BaseMapper<Reward> {
    
    @Select("SELECT * FROM tb_reward WHERE status = 1 ORDER BY points ASC")
    java.util.List<Reward> selectActiveRewards();
}
